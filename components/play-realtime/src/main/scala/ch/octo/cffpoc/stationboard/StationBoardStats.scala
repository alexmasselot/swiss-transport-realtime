package ch.octo.cffpoc.stationboard

import ch.octo.cffpoc.stops.Stop
import org.joda.time.DateTime

/**
 * Created by alex on 17/03/16.
 */
case class StationBoardStats(timestamp: DateTime,
  stop: Stop,
  total: Int,
  delayed: Int)