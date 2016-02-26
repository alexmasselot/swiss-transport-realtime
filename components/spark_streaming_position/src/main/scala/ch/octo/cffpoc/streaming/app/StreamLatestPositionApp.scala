package ch.octo.cffpoc.streaming.app

import java.io.File
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._

import ch.octo.cffpoc._
import ch.octo.cffpoc.streaming.{ serializers, TrainPositionSnapshotEncoder, TrainCFFPositionLatestCollection, TrainCFFPositionDecoder }
import com.fasterxml.jackson.databind.ser.std.StringSerializer
import com.typesafe.config.ConfigFactory
import kafka.serializer.StringDecoder
import org.apache.kafka.clients.producer.{ ProducerRecord, ProducerConfig, KafkaProducer }
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.apache.spark.{ SparkConf, SparkEnv }
import play.api.libs.json.Json
import serializers._

/**
 * Created by alex on 17/02/16.
 */
object StreamLatestPositionApp {
  val logger = LoggerFactory.getLogger(this.getClass)

  lazy val appConfig = {
    val configFile = System.getProperty("config.file", "conf/application.conf")
    println(s"loading configuration from $configFile")
    val c = ConfigFactory.parseFile(new File(configFile))

    println(c.toString)
    c

  }

  def getAppConfOrElse(path: String, default: String): String = if (appConfig.hasPath(path)) {
    appConfig.getString(path)
  } else {
    default
  }

  def initSparkStreamingContext: StreamingContext = {
    val sparkConf = new SparkConf(false) // skip loading external settings
      .setMaster(getAppConfOrElse("spark.master", "local[*]"))
      .setAppName(getAppConfOrElse("application.name", "Spark Streaming with Scala and Akka"))

    appConfig.entrySet()
      .toList
      .map(e => (e.getKey, e.getValue.unwrapped()))
      .filter(_._1.startsWith("spark"))
      .filter(_._1 != "spark.master")
      .foreach({
        case (k, v) =>
          sparkConf.set(k, v.toString)
      })
    new StreamingContext(sparkConf, Seconds(2))
  }

  def main(args: Array[String]) {

    val sparkStreamingContext = initSparkStreamingContext
    val numThreads = 2

    sparkStreamingContext.checkpoint("/tmp/streaming-slpa-checkpoint")
    logger.info(s"appConfig= $appConfig")

    val kafkaConsumerParams = Map(
      "zookeeper.connect" -> (getAppConfOrElse("zookeeper.host", "localhost") + ":" + getAppConfOrElse("zookeeper.port", "2181")),
      "group.id" -> getAppConfOrElse("kafka.consume.group.id", "stream_latest_position_app")
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
      List((getAppConfOrElse("kafka.consume.topic", "cff_train_position"), numThreads)).toMap,
      StorageLevel.MEMORY_AND_DISK_SER_2
    )
      .map(_._2)

    // Define which topics to read from

    val actorSystem = SparkEnv.get.actorSystem

    lines.window(Seconds(10), Seconds(4))
      .foreachRDD({
        rdd =>
          val n = rdd.count()
          val latestTrains = rdd.aggregate(TrainCFFPositionLatestCollection())(
            ((acc, p) => acc + p),
            ((acc, ps) => acc + ps)
          )
          val tNow: Long = System.currentTimeMillis()
          val snapshot = latestTrains.snapshot(tNow)
          println(snapshot)
          val producer = new KafkaProducer[String, String](kafkaProducerParams)

          val message = new ProducerRecord[String, String](
            getAppConfOrElse("kafka.produce.topic", "train_position_snapshot"),
            null,
            Json.toJson(snapshot).toString())
          producer.send(message)
      })

    sparkStreamingContext.start()
    sparkStreamingContext.awaitTermination()
  }
}
