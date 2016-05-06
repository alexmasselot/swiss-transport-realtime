package ch.octo.cffpoc.gtfs.raw

import ch.octo.cffpoc.gtfs.{ RawStopTime, ScheduleTime, StopId, TripId }
import org.joda.time.LocalTime

/**
 * Created by alex on 03/05/16.
 */

object RawStopTimeReader extends RawDataCollectionReader[RawStopTime] {

  override def unmap(raw: Map[String, String]): RawStopTime =
    RawStopTime(TripId(raw("trip_id")), StopId(raw("stop_id")), ScheduleTime(raw("arrival_time")), ScheduleTime(raw("departure_time")))

}