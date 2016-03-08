package ch.octo.cffpoc.stationboard

import ch.octo.cffpoc.stops.Stop
import org.joda.time.DateTime

import scala.concurrent.duration.{ Duration, FiniteDuration }

/**
 * Created by alex on 08/03/16.
 */
case class StationBoardEvent(
    timestamp: DateTime,
    stop: Stop,
    arrivalTimestamp: Option[DateTime],
    departureTimestamp: Option[DateTime],
    delayMinute: Option[Int]) {

  /**
   * return if the train is in the station with the next ?? seconds
   *
   * @param interval the time period to look forwards
   */
  def isWithin(interval: Duration): Boolean = {
    if (interval.isFinite()) {
      val until = timestamp.plus(interval.toMillis)
      (arrivalTimestamp, departureTimestamp) match {
        case (None, None) => false
        case (_, Some(t)) if t.isBefore(until) => true
        case (Some(t), _) if t.isBefore(until) => true
        case _ => false
      }
    } else {
      true
    }
  }
}
