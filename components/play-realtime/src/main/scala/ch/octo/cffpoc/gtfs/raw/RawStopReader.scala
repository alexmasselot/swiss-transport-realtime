package ch.octo.cffpoc.gtfs.raw

import java.io.File

import ch.octo.cffpoc.gtfs.{ RawStop, StopId, StopName }
import com.github.tototoshi.csv.CSVReader

/**
 * Created by alex on 02/05/16.
 */

object RawStopReader extends RawDataCollectionReader[RawStop] {

  override def unmap(raw: Map[String, String]): RawStop = RawStop(
    StopId(raw("stop_id")),
    StopName(raw("stop_name")),
    lat = raw("stop_lat").toDouble,
    lng = raw("stop_lon").toDouble
  )

}

