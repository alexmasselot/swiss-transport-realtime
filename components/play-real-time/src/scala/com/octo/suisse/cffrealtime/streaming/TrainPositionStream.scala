package com.octo.suisse.cffrealtime.streaming

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
    val ssc = new StreamingContext(SparkCommons.sparkContext, Seconds(1))
    val kafkaParams = Map[String, String]("metadata.broker.list" -> "192.168.99.101:9092")

    val kafkaStream = KafkaUtils.createDirectStream[String, TrainPosition, StringDecoder, TrainPositionDecoder](
      ssc,
      kafkaParams, Set("cff_train_position")
    )

    ssc.start()
    ssc.awaitTermination()
    kafkaStream
  }

}
