package ch.octo.cffpoc.streaming.actors

import akka.actor.{ Actor, ActorLogging, ActorPath }
import ch.octo.cffpoc.position.{ TrainCFFPosition, TrainCFFPositionLatestCollection }
import ch.octo.cffpoc.stationboard.{ StationBoard, StationBoardEvent }
import ch.octo.cffpoc.stops.Stop
import ch.octo.cffpoc.streaming.app.akka.actors.Messages.{ PositionDetails, PositionSnapshot, StationBoardDetails }
import org.joda.time.DateTime

/**
 * keeps a station board up to date a for a
 * Created by alex on 30/03/16.
 */
class PositionAggregatorActor() extends Actor with ActorLogging {
  var latestPositions = TrainCFFPositionLatestCollection()

  override def receive: Receive = {
    case tpos: TrainCFFPosition =>
      latestPositions = latestPositions + tpos
    case PositionSnapshot => sender ! latestPositions.snapshot(DateTime.now())
    case PositionDetails(id) =>
      sender ! latestPositions.get(id).map(_.current)
  }
}
