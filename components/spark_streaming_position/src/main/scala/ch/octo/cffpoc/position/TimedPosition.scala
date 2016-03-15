package ch.octo.cffpoc.position

import ch.octo.cffpoc.models.GeoLoc
import ch.octo.cffpoc.stops.Stop
import org.joda.time.DateTime

/**
 * Created by alex on 01/03/16.
 */

sealed trait HasTimedPosition {
  val timestamp: DateTime
  val position: GeoLoc
}

case class TimedPosition(
    timestamp: DateTime,
    position: GeoLoc) extends HasTimedPosition {

}

case class TimedPositionIsMoving(
    timestamp: DateTime,
    position: GeoLoc,
    moving: Boolean) extends HasTimedPosition {

}

case class TimedPositionWithStop(
    timestamp: DateTime,
    position: GeoLoc,
    stop: Option[Stop]) extends HasTimedPosition {
}