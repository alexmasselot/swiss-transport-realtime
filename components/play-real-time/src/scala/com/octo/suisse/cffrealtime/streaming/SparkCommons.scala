package com.octo.suisse.cffrealtime.streaming

import org.apache.spark.streaming.{ Seconds, StreamingContext }
import org.apache.spark.{ SparkContext, SparkConf }

/**
 * Created by alex on 31/12/15.
 */
object SparkCommons {
  val driverPort = 7777
  val driverHost = "localhost"
  lazy val conf = new SparkConf(false) // skip loading external settings
    .setMaster("local[*]") // run locally with as many threads as CPUs
    .setAppName("Spark Streaming with Scala and Akka") // name in web UI
    .set("spark.logConf", "true")
    .set("spark.driver.port", driverPort.toString)
    .set("spark.driver.host", driverHost)
    .set("spark.akka.logLifecycleEvents", "true")

  lazy val sparkContext = SparkContext.getOrCreate(conf)

}
