package ch.octo.cffpoc.streaming.app

import akka.actor.{ Props, Actor }
import ch.octo.cffpoc.position.{ TrainCFFPosition, TrainCFFPositionLatestCollection }
import ch.octo.cffpoc.stops.{ StopCloser, StopCollection }
import ch.octo.cffpoc.streaming.TrainCFFPositionDecoder
import ch.octo.cffpoc.streaming.serializers._
import kafka.serializer.StringDecoder
import org.apache.kafka.clients.producer.{ KafkaProducer, ProducerConfig, ProducerRecord }
import org.apache.spark.SparkEnv
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.apache.spark.streaming.receiver.ActorHelper
import org.joda.time.DateTime
import play.api.libs.json.Json

/**
 * Created by alex on 17/02/16.
 */
object StreamLatestPositionApp extends StreamingApp {
  val kafkaConsumeGroupId = "stream_latest_position_app"

  case class TPList(positions: Seq[TrainCFFPosition])

  class TPAggregatorActor extends Actor with ActorHelper {
    var latestPosition = TrainCFFPositionLatestCollection()

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
      println("=== TPAggregatorActor is starting up ===")
      println(s"=== path=${context.self.path} ===")
      println("")
    }

    def receive = {
      //         store() method allows us to store the message so Spark Streaming knows about it
      //         This is the integration point (from Akka's side) between Spark Streaming and Akka
      case tplist: TPList =>
        latestPosition = latestPosition + tplist.positions
        log.warn(s"added ${tplist.positions.size} events")

        val timestamp = tplist.positions match {
          case Nil if latestPosition.size == 0 => DateTime.now()
          case Nil => latestPosition.toList.map(_.current.timedPosition.timestamp).maxBy(_.getMillis)
          case xs =>
            val t = tplist.positions.last.current.timedPosition.timestamp
            latestPosition = latestPosition.after(t.minusSeconds(60))
            t
        }

        val snapshot = latestPosition.snapshot(timestamp)
        val message = new ProducerRecord[String, String](
          getAppConfOrElse("kafka.train_position.produce.topic", "train_position_snapshot"),
          null,
          Json.toJson(snapshot).toString()) //Json.toJson(snapshot).toString())
        producer.send(message)

      case x => log.warn(s"unmatched message $x")
    }
  }

  def main(args: Array[String]) {

    val sparkStreamingContext = initSparkStreamingContext
    val numThreads = 2

    //sparkStreamingContext.checkpoint("/tmp/streaming-slpa-checkpoint")
    logger.info(s"appConfig= $appConfig")

    val actorName = "station-board-aggregator"
    val actorSystem = SparkEnv.get.actorSystem
    val props = Props[TPAggregatorActor]
    val aggregActor = actorSystem.actorOf(props, actorName)

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

    val closer = new StopCloser(StopCollection.load(getClass.getResourceAsStream("/stops.txt")), 500)

    lines //.window(Seconds(10), Seconds(4))
      .foreachRDD({
        rdd =>
          val n = rdd.count()
          aggregActor ! TPList(rdd.collect())
      })

    sparkStreamingContext.start()
    sparkStreamingContext.awaitTermination()
  }
}
