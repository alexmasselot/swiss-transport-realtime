package ch.octo.cffpoc.gtfs.raw

import java.io.File

import ch.octo.cffpoc.gtfs.{ ServiceId, RawCalendarDate }
import com.github.tototoshi.csv.CSVReader

/**
 * Created by alex on 03/05/16.
 */
trait RawDataCollectionReader[T] {
  def unmap(raw: Map[String, String]): T

  def load(filename: String): List[T] = {
    val cvsreader = CSVReader.open(new File(filename))
    cvsreader.allWithHeaders()
      .map(l => unmap(l))
  }
}
