package ch.octo.cffpoc.models

import ch.octo.cffpoc.stops.Stop

/**
 * Created by alex on 01/03/16.
 */

sealed trait HasTimedPosition {
  val timestamp: Long
  val position: GeoLoc
}

case class TimedPosition(
    timestamp: Long,
    position: GeoLoc) extends HasTimedPosition {

}

case class TimedPositionIsMoving(
    timestamp: Long,
    position: GeoLoc,
    moving: Boolean) extends HasTimedPosition {

}

case class TimedPositionWithStop(timestamp: Long,
    position: GeoLoc,
    stop: Option[Stop]) extends HasTimedPosition {

}