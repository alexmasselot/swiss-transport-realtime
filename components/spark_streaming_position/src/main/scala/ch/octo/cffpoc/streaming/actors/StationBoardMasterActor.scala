package ch.octo.cffpoc.streaming.app.akka.actors

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import akka.pattern.ask
import akka.util.Timeout
import ch.octo.cffpoc.stationboard.{ StationBoard, StationBoardEvent }
import ch.octo.cffpoc.stops.Stop
import ch.octo.cffpoc.streaming.app.akka.actors.Messages.{ StationBoardDetails, StationBoardsSnapshot }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
 * Created by alex on 30/03/16.
 */
class StationBoardMasterActor extends Actor with ActorLogging {

  implicit val timeout = Timeout(5 seconds)

  val statsActor = context.actorOf(Props[StationBoardStatsActor], "stats")

  def stationBoardActorName(stop: Stop) = s"stop-${stop.id}"

  def stationBoardActorName(stopId: Long) = s"stop-${stopId}"

  def stationBoardActor(stop: Stop): ActorRef =
    context
      .child(stationBoardActorName(stop))
      .getOrElse(context.actorOf(Props(classOf[StationBoardActor], stop, statsActor.path), stationBoardActorName(stop)))

  def stationBoardActor(stopId: Long): Option[ActorRef] =
    context
      .child(stationBoardActorName(stopId))

  override def preStart = {

  }

  override def receive: Receive = {
    case evt: StationBoardEvent =>
      stationBoardActor(evt.stop) ! evt
    case StationBoardsSnapshot => statsActor forward StationBoardsSnapshot
    case StationBoardDetails(stopId) => stationBoardActor(stopId) match {
      case Some(sba) =>
        val origSender = sender
        (sba ? StationBoardDetails(stopId)).mapTo[StationBoard].map(r => origSender ! r)
      case None => sender ! None
    }
  }
}
