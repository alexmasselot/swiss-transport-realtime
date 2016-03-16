package ch.octo.cffpoc.streaming.app

import ch.octo.cffpoc.position.TrainCFFPosition
import ch.octo.cffpoc.stationboard.StationBoardEvent

/**
 * Created by alex on 15/03/16.
 */
package object actors {
  case class TPList(positions: Seq[TrainCFFPosition])
  case class SBEventList(events: List[StationBoardEvent]) {}

}
