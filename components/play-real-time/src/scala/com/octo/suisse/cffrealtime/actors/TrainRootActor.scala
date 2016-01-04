package com.octo.suisse.cffrealtime.actors

import akka.actor.{ ActorLogging, Actor, Props }
import akka.util.Timeout
import com.octo.suisse.cffrealtime.models.TrainPosition
import com.octo.suisse.cffrealtime.streaming.{ SparkCommons, TrainPositionKafkaStream }
import scala.concurrent.duration._
import akka.pattern.ask
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by alex on 31/12/15.
 */
class TrainRootActor extends Actor with ActorLogging {
  implicit val timeout = Timeout(5 seconds)

  println("in TrainRootActor")
  val aAllTrains = context.actorOf(Props[AllTrainPositionActor], "all-trains")

  override def receive: Receive = {
    case "INIT" =>
      val aKafka = context.actorOf(Props[KafkaTrainPositionActor], "kafka-position")
      aKafka ! "INIT"
    case x => log.info(s"WTF ROOT $x")
  }

}
