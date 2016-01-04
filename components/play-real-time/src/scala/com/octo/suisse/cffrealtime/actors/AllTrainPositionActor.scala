package com.octo.suisse.cffrealtime.actors

import akka.actor.{ ActorLogging, Actor }
import akka.actor.Actor.Receive
import akka.serialization.Serialization
import com.octo.suisse.cffrealtime.models.TrainPosition

import scala.collection.mutable

/**
 * Created by alex on 31/12/15.
 */
class AllTrainPositionActor extends Actor with ActorLogging {
  val trainPositions = mutable.Map[String, TrainPosition]()
  override def receive: Receive = {
    case "PATH" => sender ! Serialization.serializedActorPath(self)
    case tp: TrainPosition => {
      log.info(s"received train position $tp")
      trainPositions.put(tp.train.id, tp)
    }
    case tps: List[TrainPosition] => {
      log.info(s"received train list ${tps.size}")
      tps.foreach(tp => trainPositions.put(tp.train.id, tp))
    }
    case "ALL" => sender ! trainPositions.toList
    case x => log.info(s"WTF $x")
  }
}
