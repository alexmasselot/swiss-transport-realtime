package ch.octo.cffpoc.gtfs.raw

import java.io.File

import ch.octo.cffpoc.gtfs._
import com.github.tototoshi.csv.CSVReader

/**
 * Created by alex on 02/05/16.
 */

object RawTripReader extends RawDataCollectionReader[RawTrip] {

  override def unmap(row: Map[String, String]): RawTrip = RawTrip(
    RouteId(row("route_id")),
    ServiceId(row("service_id")),
    TripId(row("trip_id")),
    StopName(row("trip_headsign")),
    TripShortName(row("trip_short_name"))
  )

}
