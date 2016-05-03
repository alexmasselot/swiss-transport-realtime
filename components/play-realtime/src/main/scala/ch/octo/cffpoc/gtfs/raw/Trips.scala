package ch.octo.cffpoc.gtfs.raw

import java.io.File

import ch.octo.cffpoc.gtfs._
import com.github.tototoshi.csv.CSVReader

/**
 * Created by alex on 02/05/16.
 */
class Trips(val list: List[RawTrip]) {
  def size = list.size

}

object Trips {
  def load(filename: String): Trips = {
    val cvsreader = CSVReader.open(new File(filename))
    val l = cvsreader.allWithHeaders()
      .map(l => RawTrip(
        RouteId(l("route_id")),
        ServiceId(l("service_id")),
        TripId(l("trip_id")),
        StopName(l("trip_headsign")),
        TripShortName(l("trip_short_name"))
      ))
    new Trips(l)
  }
}
