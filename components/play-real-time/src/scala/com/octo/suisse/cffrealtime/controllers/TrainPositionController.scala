package com.octo.suisse.cffrealtime.controllers

import play.api.Play.current
import com.octo.suisse.cffrealtime.actors.ws.MyWebSocketActor
import com.octo.suisse.cffrealtime.models.TrainPosition
import com.octo.suisse.cffrealtime.streaming.TrainPositionStream
import org.apache.spark.{ SparkContext, SparkConf }
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{ Seconds, StreamingContext }
import play.api.libs.json.JsValue
import play.api.mvc.{ WebSocket, Action, Controller }

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
    Ok("paf")
  }

  def pif = WebSocket.acceptWithActor[String, String] { request =>
    out =>
      TrainPositionStream.stream
      println("DAMN IN acceptWithActor")

      val s = MyWebSocketActor.props(out)

      s
  }

}
