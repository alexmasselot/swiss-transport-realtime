package com.octo.suisse.cffrealtime.controllers

import akka.actor.{ ActorSystem, Props }
import com.octo.suisse.cffrealtime.actors.{ TrainRootActor, AllTrainPositionActor }
import play.api.Play.current
import com.octo.suisse.cffrealtime.actors.ws.MyWebSocketActor
import com.octo.suisse.cffrealtime.models.TrainPosition
import com.octo.suisse.cffrealtime.streaming.{ SparkCommons, TrainPositionKafkaStream }
import org.apache.spark.{ SparkContext, SparkConf }
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{ Seconds, StreamingContext }
import play.api.libs.json.JsValue
import play.api.mvc.{ WebSocket, Action, Controller }

/**
 * Created by alex on 30/12/15.
 */
object TrainPositionController extends Controller {
  println("in TrainPositionController")

  def index = Action {
    Ok("Paf le chien")
  }

  def paf = Action {
    // Configuration for a Spark application.
    val rootActor = ActorSystem("TrainSystem").actorOf(Props[TrainRootActor], "train-root")
    rootActor ! "INIT"
    Ok("paf")
  }

  def pif = WebSocket.acceptWithActor[String, String] { request =>
    out =>
      //TrainPositionKafkaStream.stream
      println("DAMN IN acceptWithActor")

      val s = MyWebSocketActor.props(out)

      s
  }

}
