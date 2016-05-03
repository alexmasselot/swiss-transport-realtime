package ch.octo.cffpoc.gtfs.raw

import java.io.File

import ch.octo.cffpoc.gtfs.{ RawSTop, StopId, StopName }
import com.github.tototoshi.csv.CSVReader

/**
 * Created by alex on 02/05/16.
 */
class Stops(val list: List[RawSTop]) {
  def size = list.size

}
object Stops {
  def load(filename: String): Stops = {
    val cvsreader = CSVReader.open(new File(filename))
    val m = cvsreader.allWithHeaders()
      .map(l => RawSTop(
        StopId(l("stop_id")),
        StopName(l("stop_name")),
        lat = l("stop_lat").toDouble,
        lng = l("stop_lon").toDouble
      ))
    new Stops(m)
  }
}

