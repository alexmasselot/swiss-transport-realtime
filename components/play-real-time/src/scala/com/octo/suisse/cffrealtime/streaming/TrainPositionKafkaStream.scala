package com.octo.suisse.cffrealtime.streaming

import com.octo.suisse.cffrealtime.models.{ Train, GeoCoordinates, TrainPosition }
import kafka.serializer.{ Decoder, StringDecoder }
import kafka.utils.VerifiableProperties
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{ Seconds, StreamingContext }
import play.api.libs.json.{ JsPath, Reads, Json }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by alex on 30/12/15.
 */
object TrainPositionKafkaStream {

  def stream(cb: (List[TrainPosition] => Unit)) = {
    import play.api.libs.json._
    // JSON library
    import play.api.libs.json.Reads._
    // Custom validation helpers
    import play.api.libs.functional.syntax._
    // Combinator syntax

    println("in TrainPositionStream.stream")
    val ssc = new StreamingContext(SparkCommons.sparkContext, Seconds(1))

    val kafkaParams = Map[String, String]("metadata.broker.list" -> "192.168.99.101:9092")

    val kafkaStream = KafkaUtils.createDirectStream[String, TrainPosition, StringDecoder, TrainPositionDecoder](
      ssc,
      kafkaParams, Set("cff_train_position")
    )

    //ssc.actorStream(Props[AllTrainPositionActor], "all-trains")
    println("launching kafka stream")
    kafkaStream.foreachRDD({ rdd =>
      println(s"RDD ${rdd.count()}")
      if (!rdd.isEmpty()) {
        println(s"DUH ${rdd.count()}")
        val l = rdd.toLocalIterator.toList;
        println(l.size);
        //       println("HAHAH")
        //cb(List()) //)
      }
    })
    ssc.start()
    ssc.awaitTermination()
    println("finished")
  }

}
