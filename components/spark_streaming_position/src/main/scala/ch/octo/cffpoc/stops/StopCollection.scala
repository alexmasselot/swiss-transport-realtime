package ch.octo.cffpoc.stops

import java.io.File

import ch.octo.cffpoc.models.GeoLoc
import com.github.tototoshi.csv.CSVReader

import scala.io.Source

/**
 * Created by alex on 26/02/16.
 */
class StopCollection(stops: List[Stop]) {
  def size = stops.size

  def toList = stops
}

object StopCollection {
  /**
   * laod an exported CFF stops files, and load all stops.
   * As platform specific are described, we onl keep the platformless pointer
   *
   * @param filename
   * @return
   */
  def load(filename: String): StopCollection = {

    val reader = CSVReader.open(new File(filename))
    val itLines = Source.fromFile(filename, "UTF8").getLines()
    val lStops = reader.allWithHeaders()
      .groupBy(m => m("stop_id").replaceAll(":.*", ""))
      .map({
        case (k, v) =>
          v.sortBy(_("stop_id")).head
      })
      .map(m => Stop(
        m("stop_id").replaceAll(":.*", "").toLong,
        m("stop_name"),
        GeoLoc(m("stop_lat").toDouble, m("stop_lon").toDouble))
      )
      .toList

    new StopCollection(lStops)
  }
}
