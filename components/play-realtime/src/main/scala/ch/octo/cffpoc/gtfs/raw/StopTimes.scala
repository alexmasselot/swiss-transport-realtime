package ch.octo.cffpoc.gtfs.raw

import java.io.File

import ch.octo.cffpoc.gtfs.{ RawStopTime, StopId, TripId }
import com.github.tototoshi.csv.CSVReader
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat

/**
 * Created by alex on 03/05/16.
 */
class StopTimes(val list: List[RawStopTime]) {
  def size = list.size
}

object StopTimes {
  def load(filename: String): StopTimes = {
    val cvsreader = CSVReader.open(new File(filename))
    val l = cvsreader.allWithHeaders()
      .map(l => RawStopTime(TripId(l("trip_id")), StopId(l("stop_id")), timeFromString(l("arrival_time")), timeFromString(l("departure_time"))))
    new StopTimes(l)
  }

  val dateFormatter = DateTimeFormat.forPattern("HH:mm:ss")

  def timeFromString(s: String): LocalTime = {
    LocalTime.parse(s, dateFormatter)
  }

}