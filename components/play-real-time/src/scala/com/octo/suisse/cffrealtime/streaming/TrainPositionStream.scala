package com.octo.suisse.cffrealtime.streaming

import akka.actor.Props
import com.octo.suisse.cffrealtime.actors.AllTrainPositionActor
import com.octo.suisse.cffrealtime.models.TrainPosition
import kafka.serializer.StringDecoder
import org.apache.spark.{ SparkContext, SparkConf }
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{ Seconds, StreamingContext }

/**
 * Created by alex on 30/12/15.
 */
object TrainPositionStream {

  lazy val stream = {
    println("in TrainPositionStream.stream")
    val aAllTrains = SparkCommons.actorySystem.actorOf(Props[AllTrainPositionActor], "all-trains")

    val ssc = new StreamingContext(SparkCommons.sparkContext, Seconds(1))

    val kafkaParams = Map[String, String]("metadata.broker.list" -> "192.168.99.101:9092")

    val kafkaStream = KafkaUtils.createDirectStream[String, TrainPosition, StringDecoder, TrainPositionDecoder](
      ssc,
      kafkaParams, Set("cff_train_position")
    )

    //ssc.actorStream(Props[AllTrainPositionActor], "all-trains")
    kafkaStream.foreachRDD({ rdd =>
      if (!rdd.isEmpty()) {
        aAllTrains ! rdd.collect.toList.map(_._2)
      }
    })
    ssc.start()
    ssc.awaitTermination()
    0
  }

}
