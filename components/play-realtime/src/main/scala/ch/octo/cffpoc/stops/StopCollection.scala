package ch.octo.cffpoc.stops

import java.io.{ InputStreamReader, InputStream, File }

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
  def load(reader: CSVReader, simplify: Boolean): StopCollection = {
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

  /**
   * laod an exported CFF stops files, and load all stops.
   * As platform specific are described, we onl keep the platformless pointer
   *
   * @param filename the stops.txt file to load from
   * @param simplify if true (the default), the platforms numbers are remove 1234:5, 1234:6 are mapped to 1234
   * @return
   */
  def load(filename: String, simplify: Boolean): StopCollection = load(CSVReader.open(new File(filename)), simplify)

  def load(input: InputStream, simplify: Boolean): StopCollection = load(CSVReader.open(new InputStreamReader(input)), simplify)

}
