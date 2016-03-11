package ch.octo.cffpoc.streaming.app

import akka.actor.{ Actor, Props }
import ch.octo.cffpoc.stationboard.{ StationBoardsSnapshot, StationBoardEvent, StationBoardEventDecoder }
import ch.octo.cffpoc.stops.{ StopCloser, StopCollection }
import ch.octo.cffpoc.streaming.app.StreamLatestPositionApp._
import kafka.serializer.StringDecoder
import org.apache.kafka.clients.producer.{ ProducerRecord, KafkaProducer, ProducerConfig }
import org.apache.spark.SparkEnv
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.apache.spark.streaming.receiver.ActorHelper
import play.api.libs.json.Json

/**
 * Created by alex on 17/02/16.
 */
object StreamLatestStationBoardsApp extends StreamingApp {
  val kafkaConsumeGroupId = "stream_latest_stationboard_app"

  def main(args: Array[String]): Unit = {

    val sparkStreamingContext = initSparkStreamingContext
    val numThreads = 2

    val actorName = "helloer"

    case class SBEventList(events: List[StationBoardEvent]) {}

    class SBAggregatorActor extends Actor with ActorHelper {
      var boards = StationBoardsSnapshot()

      val kafkaProducerParams = new java.util.HashMap[String, Object]()
      kafkaProducerParams.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
        getAppConfOrElse("kafka.host", "localhost") + ":" + getAppConfOrElse("kafka.port", "2181"))
      kafkaProducerParams.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        "org.apache.kafka.common.serialization.StringSerializer")
      kafkaProducerParams.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        "org.apache.kafka.common.serialization.StringSerializer")

      val producer = new KafkaProducer[String, String](kafkaProducerParams)

      override def preStart() = {
        println("")
        println("=== Helloer is starting up ===")
        println(s"=== path=${context.self.path} ===")
        println("")
      }

      def receive = {
        //         store() method allows us to store the message so Spark Streaming knows about it
        //         This is the integration point (from Akka's side) between Spark Streaming and Akka
        case evts: SBEventList =>
          evts.events.foreach(e => boards = boards + e)
          log.warn(s"added ${evts.events.size} events")

          if (evts.events.lastOption.isDefined) {
            val t = evts.events.last.timestamp
            logger.warn("removing: " + boards.before(t.minusMinutes(1)).countAll)
            boards = boards.after(t.minusMinutes(1)).before(t.plusHours(1))
          }

          //println(boards.boards.values.map(_.stop.name).toList.sorted)
          println(boards.get("Lausanne"))

          val message = new ProducerRecord[String, String](
            getAppConfOrElse("kafka.station_board.produce.topic", "station_board_snapshot"),
            null,
            "BADABOUM " + boards.size + "/" + boards.countAll) //Json.toJson(snapshot).toString())
          producer.send(message)

        case x => log.warn(s"unmatched message $x")
      }
    }
    //sparkStreamingContext.checkpoint("/tmp/streaming-slpa-checkpoint")
    logger.info(s"appConfig= $appConfig")

    val kafkaConsumerParams = Map(
      "zookeeper.connect" -> (getAppConfOrElse("zookeeper.host", "localhost") + ":" + getAppConfOrElse("zookeeper.port", "2181")),
      "group.id" -> getAppConfOrElse("kafka.station_board.consume.group.id", "stream_latest_position_app")
    )

    logger.info(s"kafka consumer params: $kafkaConsumerParams")

    val events = KafkaUtils.createStream[String, StationBoardEvent, StringDecoder, StationBoardEventDecoder](
      sparkStreamingContext,
      kafkaConsumerParams,
      List((getAppConfOrElse("kafka.station_board.consume.topic", ""), numThreads)).toMap,
      StorageLevel.MEMORY_ONLY //MEMORY_AND_DISK_SER_2
    )
      .map(_._2)

    // Define which topics to read from

    val actorSystem = SparkEnv.get.actorSystem
    val props = Props[SBAggregatorActor]
    val helloActor = actorSystem.actorOf(props, actorName)

    val closer = new StopCloser(StopCollection.load(getClass.getResourceAsStream("/stops.txt")), 500)

    events //.window(Seconds(10), Seconds(4))
      .foreachRDD({
        rdd =>

          val n = rdd.count()
          helloActor ! SBEventList(rdd.collect().toList)

      })

    sparkStreamingContext.start()
    sparkStreamingContext.awaitTermination()
  }
}
