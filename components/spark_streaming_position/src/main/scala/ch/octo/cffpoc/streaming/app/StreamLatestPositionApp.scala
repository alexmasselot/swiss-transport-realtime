package ch.octo.cffpoc.streaming.app

import ch.octo.cffpoc.TrainCFFPosition
import ch.octo.cffpoc.streaming.{ TrainCFFPositionLatestCollection, TrainCFFPositionDecoder }
import kafka.serializer.StringDecoder
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.apache.spark.{ SparkConf, SparkEnv }

/**
 * Created by alex on 17/02/16.
 */
object StreamLatestPositionApp {
  def main(args: Array[String]) {

    val (zkQuorum, group, numThreads) = ("192.168.99.100:2181", "pipo-sk", 2)

    // Configuration for a Spark application.
    // Used to set various Spark parameters as key-value pairs.
    val driverPort = 7777
    val driverHost = "localhost"
    val conf = new SparkConf(false) // skip loading external settings
      .setMaster("local[*]") // run locally with as many threads as CPUs
      .setAppName("Spark Streaming with Scala and Akka") // name in web UI
      .set("spark.logConf", "true")
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
          println("counting rdd", n)
      })

    ssc.start()
    ssc.awaitTermination()
  }
}
