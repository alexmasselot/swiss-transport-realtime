package ch.octo.cffpoc.streaming.app.akka.actors

import akka.actor.Actor.Receive
import akka.actor.{ ActorSystem, ActorPath, ActorLogging, Actor }
import ch.octo.cffpoc.stationboard.{ StationBoard, StationBoardEvent }
import ch.octo.cffpoc.stops.Stop
import ch.octo.cffpoc.streaming.app.akka.actors.Messages.{ StationBoardDetails, StationBoardsSnapshot }
import org.joda.time.DateTime

/**
 * keeps a station board up to date a for a
 * Created by alex on 30/03/16.
 */
class StationBoardActor(stop: Stop, statsActorPath: ActorPath) extends Actor with ActorLogging {

  val statsActor = context.actorSelection(statsActorPath)

  var stationBoard = StationBoard(DateTime.now(), stop)

  override def receive: Receive = {
    case evt: StationBoardEvent =>
      stationBoard = (stationBoard + evt).after(evt.timestamp.minusMinutes(2)).before(evt.timestamp.plusMinutes(20))
      statsActor ! stationBoard.stats

    case _: StationBoardDetails => sender ! stationBoard
  }
}
