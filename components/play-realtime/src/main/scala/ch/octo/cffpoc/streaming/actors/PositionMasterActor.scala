package ch.octo.cffpoc.streaming.actors

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import akka.pattern.ask
import akka.util.Timeout
import ch.octo.cffpoc.position.TrainCFFPosition
import ch.octo.cffpoc.stationboard.{ StationBoard, StationBoardEvent }
import ch.octo.cffpoc.stops.Stop
import ch.octo.cffpoc.streaming.app.akka.actors.Messages.{ PositionDetails, PositionSnapshot, StationBoardsSnapshot, StationBoardDetails }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
 * Created by alex on 30/03/16.
 */
class PositionMasterActor extends Actor with ActorLogging {

  implicit val timeout = Timeout(5 seconds)

  val positionAggregatorActor = context.actorOf(Props(classOf[PositionAggregatorActor]), "aggregator")

  override def preStart = {

  }

  override def receive: Receive = {
    case tpos: TrainCFFPosition =>
      positionAggregatorActor ! tpos
    case PositionSnapshot => positionAggregatorActor forward PositionSnapshot
    case d: PositionDetails => positionAggregatorActor forward (d)

  }
}
