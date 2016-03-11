package ch.octo.cffpoc.streaming.app

import java.io.File

import akka.actor.Actor
import ch.octo.cffpoc.models.TrainCFFPosition
import ch.octo.cffpoc.stationboard.{ StationBoardsSnapshot, StationBoardEvent }
import ch.octo.cffpoc.stops.{ StopCloser, StopCollection }
import ch.octo.cffpoc.streaming.app.StreamLatestStationBoardsApp._
import ch.octo.cffpoc.streaming.serializers._
import ch.octo.cffpoc.streaming.{ TrainCFFPositionDecoder, TrainCFFPositionLatestCollection, serializers }
import com.typesafe.config.ConfigFactory
import kafka.serializer.StringDecoder
import org.apache.kafka.clients.producer.{ KafkaProducer, ProducerConfig, ProducerRecord }
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.apache.spark.streaming.receiver.ActorHelper
import org.apache.spark.{ SparkConf, SparkEnv }
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import play.api.libs.json.Json

import scala.collection.JavaConversions._

/**
 * Created by alex on 17/02/16.
 */
object StreamLatestPositionApp extends StreamingApp {
  val kafkaConsumeGroupId = "stream_latest_position_app"

  //  case class TPList(positions: List[TrainCFFPosition])
  //
  //  class TPAggregatorActor extends Actor with ActorHelper {
  //    var boards = StationBoardCollection()
  //
  //    val kafkaProducerParams = new java.util.HashMap[String, Object]()
  //    kafkaProducerParams.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
  //      getAppConfOrElse("kafka.host", "localhost") + ":" + getAppConfOrElse("kafka.port", "2181"))
  //    kafkaProducerParams.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
  //      "org.apache.kafka.common.serialization.StringSerializer")
  //    kafkaProducerParams.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
  //      "org.apache.kafka.common.serialization.StringSerializer")
  //
  //    val producer = new KafkaProducer[String, String](kafkaProducerParams)
  //
  //    override def preStart() = {
  //      println("")
  //      println("=== Helloer is starting up ===")
  //      println(s"=== path=${context.self.path} ===")
  //      println("")
  //    }
  //
  //    def receive = {
  //      //         store() method allows us to store the message so Spark Streaming knows about it
  //      //         This is the integration point (from Akka's side) between Spark Streaming and Akka
  //      case evts: TPList =>
  //        evts.events.foreach(e => boards = boards + e)
  //        log.warn(s"added ${evts.events.size} events")
  //
  //        if (evts.events.lastOption.isDefined) {
  //          val t = evts.events.last.timestamp
  //          logger.warn("removing: " + boards.before(t.minusMinutes(1)).countAll)
  //          boards = boards.after(t.minusMinutes(1)).before(t.plusHours(1))
  //        }
  //
  //        //println(boards.boards.values.map(_.stop.name).toList.sorted)
  //        println(boards.get("Lausanne"))
  //
  //        val message = new ProducerRecord[String, String](
  //          getAppConfOrElse("kafka.station_board.produce.topic", "station_board_snapshot"),
  //          null,
  //          "BADABOUM " + boards.size + "/" + boards.countAll) //Json.toJson(snapshot).toString())
  //        producer.send(message)
  //
  //      case x => log.warn(s"unmatched message $x")
  //    }
  //  }

  def main(args: Array[String]) {

    val sparkStreamingContext = initSparkStreamingContext
    val numThreads = 2

    //sparkStreamingContext.checkpoint("/tmp/streaming-slpa-checkpoint")
    logger.info(s"appConfig= $appConfig")

    val kafkaConsumerParams = Map(
      "zookeeper.connect" -> (getAppConfOrElse("zookeeper.host", "localhost") + ":" + getAppConfOrElse("zookeeper.port", "2181")),
      "group.id" -> getAppConfOrElse("kafka.train_position.consume.group.id", "stream_latest_position_app")
    )

    logger.info(s"kafka consumer params: $kafkaConsumerParams")

    val kafkaProducerParams = new java.util.HashMap[String, Object]()
    kafkaProducerParams.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
      getAppConfOrElse("kafka.host", "localhost") + ":" + getAppConfOrElse("kafka.port", "2181"))
    kafkaProducerParams.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringSerializer")
    kafkaProducerParams.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringSerializer")

    val lines = KafkaUtils.createStream[String, TrainCFFPosition, StringDecoder, TrainCFFPositionDecoder](
      sparkStreamingContext,
      kafkaConsumerParams,
      List((getAppConfOrElse("kafka.train_position.consume.topic", "cff_train_position"), numThreads)).toMap,
      StorageLevel.MEMORY_AND_DISK_SER_2
    )
      .map(_._2)

    // Define which topics to read from

    val actorSystem = SparkEnv.get.actorSystem

    val closer = new StopCloser(StopCollection.load(getClass.getResourceAsStream("/stops.txt")), 500)

    lines.window(Seconds(10), Seconds(4))
      .foreachRDD({
        rdd =>
          val n = rdd.count()
          val latestTrains = rdd.aggregate(TrainCFFPositionLatestCollection())(
            ((acc, p) => acc + p),
            ((acc, ps) => acc + ps)
          )
          val tNow = DateTime.now()
          val snapshot = latestTrains.snapshot(tNow).closedBy(closer)
          println(snapshot)
          val producer = new KafkaProducer[String, String](kafkaProducerParams)

          val message = new ProducerRecord[String, String](
            getAppConfOrElse("kafka.train_position.produce.topic", "train_position_snapshot"),
            null,
            Json.toJson(snapshot).toString())
          producer.send(message)
      })

    sparkStreamingContext.start()
    sparkStreamingContext.awaitTermination()
  }
}
