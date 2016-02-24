package ch.octo.cffpoc.streaming.app

import ch.octo.cffpoc._
import ch.octo.cffpoc.streaming.{ serializers, TrainPositionSnapshotEncoder, TrainCFFPositionLatestCollection, TrainCFFPositionDecoder }
import com.fasterxml.jackson.databind.ser.std.StringSerializer
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
  def main(args: Array[String]) {

    val numThreads = 2

    // Configuration for a Spark application.
    // Used to set various Spark parameters as key-value pairs.
    val driverPort = 7777
    val driverHost = "localhost"
    val conf = new SparkConf(false) // skip loading external settings
      .setMaster("local[*]") // run locally with as many threads as CPUs
      .setAppName("Spark Streaming with Scala and Akka") // name in web UI
      .set("spark.logConf", "false")
      .set("spark.driver.port", driverPort.toString)
      .set("spark.driver.host", driverHost)
      .set("spark.akka.logLifecycleEvents", "true")
    val ssc = new StreamingContext(conf, Seconds(2))
    ssc.checkpoint("/tmp/streaming-slpa-checkpoint")
    val actorName = "helloer"

    val kafkaParams = Map(
      "zookeeper.connect" -> "192.168.99.100:2181",
      "group.id" -> "StreamLatestPositionApp"
    )

    val producerProps = new java.util.HashMap[String, Object]()
    producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.99.100:9092")
    producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringSerializer")
    producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringSerializer")

    val lines = KafkaUtils.createStream[String, TrainCFFPosition, StringDecoder, TrainCFFPositionDecoder](
      ssc,
      kafkaParams,
      List(("cff_train_position", numThreads)).toMap,
      StorageLevel.MEMORY_AND_DISK_SER_2
    )
      .map(_._2)

    // Define which topics to read from
    val topics = Set("cff_train_position")

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
          println("counting rdd", n)
          val producer = new KafkaProducer[String, String](producerProps)
          val message = new ProducerRecord[String, String]("duh", null, Json.toJson(snapshot).toString())
          producer.send(message)
      })

    ssc.start()
    ssc.awaitTermination()
  }
}
