package ch.octo.cffpoc.streaming.app

import java.io.File

import ch.octo.cffpoc.streaming.app.StreamLatestStationBoardsApp._
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{ Seconds, StreamingContext }
import org.slf4j.LoggerFactory
import scala.collection.JavaConversions._

/**
 * Created by alex on 08/03/16.
 */
trait StreamingApp {
  def kafkaConsumeGroupId: String

  val numThreads = 2

  val logger = LoggerFactory.getLogger(this.getClass)

  lazy val appConfig = {
    val configFile = System.getProperty("config.file", "conf/application.conf")
    logger.info(s"loading configuration from $configFile")
    val c = ConfigFactory.parseFile(new File(configFile))

    logger.debug(c.toString)
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

  lazy val kafkaConsumerParams = Map(
    "zookeeper.connect" -> (getAppConfOrElse("zookeeper.host", "localhost") + ":" + getAppConfOrElse("zookeeper.port", "2181")),
    "group.id" -> getAppConfOrElse("kafka.consume.group.id", kafkaConsumeGroupId)
  )

  lazy val kafkaProducerParams = {
    val m = new java.util.HashMap[String, Object]()
    m.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
      getAppConfOrElse("kafka.host", "localhost") + ":" + getAppConfOrElse("kafka.port", "2181"))
    m.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringSerializer")
    m.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringSerializer")
    m
  }

}
