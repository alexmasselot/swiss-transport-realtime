package com.octo.suisse.cffrealtime.controllers

import com.octo.suisse.cffrealtime.streaming.TrainPositionStream
import org.apache.spark.{ SparkContext, SparkConf }
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{ Seconds, StreamingContext }
import play.api.mvc.{ Action, Controller }

/**
 * Created by alex on 30/12/15.
 */
object TrainPositionController extends Controller {

  def index = Action {
    Ok("Paf le chien")
  }

  def paf = Action {
    // Configuration for a Spark application.
    TrainPositionStream.stream
    Ok("pipo")
  }
}
