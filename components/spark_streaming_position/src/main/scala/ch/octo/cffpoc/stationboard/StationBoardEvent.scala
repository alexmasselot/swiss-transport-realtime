package ch.octo.cffpoc.stationboard

import ch.octo.cffpoc.stops.Stop

/**
 * Created by alex on 08/03/16.
 */
case class StationBoardEvent(timestamp: Long, stop: Stop, arrivalTimestamp: Option[Long], departureTimestamp: Option[Long], delayMinute: Option[Int]) {

}
