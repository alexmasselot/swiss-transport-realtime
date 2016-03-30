package ch.octo.cffpoc.streaming.app.akka.actors

import akka.actor.{ Actor, ActorLogging }
import ch.octo.cffpoc.stationboard.{ StationBoardStats, StationBoardsSnapshotStats }
import ch.octo.cffpoc.streaming.app.akka.actors.Messages.GetGlobalStats

/**
 *
 * Handles the number of train announced, delayed and train per stop
 * no more details than that
 * Created by alex on 30/03/16.
 */
class StationBoardStatsActor extends Actor with ActorLogging {
  var snapshot = StationBoardsSnapshotStats()

  override def receive: Receive = {
    //append one new stats
    case stats: StationBoardStats =>
      snapshot = snapshot + stats

    //asks for the the global snapshot state in return
    case GetGlobalStats => sender ! snapshot
  }

}
