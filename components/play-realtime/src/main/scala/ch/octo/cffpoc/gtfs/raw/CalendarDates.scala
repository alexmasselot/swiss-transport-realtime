package ch.octo.cffpoc.gtfs.raw

import java.io.File

import ch.octo.cffpoc.gtfs.{ RawCalendarDate, ServiceId }
import com.github.tototoshi.csv.CSVReader
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

/**
 * Created by alex on 29/04/16.
 */
class CalendarDates(list: List[RawCalendarDate]) {

  def size: Int = list.size

  //  def getExceptions(date: LocalDate): Set[ServiceId] = dict.get(date) match {
  //    case None => Set.empty
  //    case Some(s) => s
  //  }
  //
  //  def countExceptions(date: LocalDate) = dict.get(date) match {
  //    case Some(s) => s.size
  //    case None => 0
  //  }
  //
  //  def isRunning(serviceId: ServiceId, date: LocalDate): Boolean = {
  //    dict.get(date) match {
  //      case Some(s) => !s.contains(serviceId)
  //      case None => true
  //    }
  //  }
}

object CalendarDates {
  def load(filename: String): CalendarDates = {
    val cvsreader = CSVReader.open(new File(filename))
    val l = cvsreader.allWithHeaders()
      .map(l => RawCalendarDate(ServiceId(l("service_id")), dateFromString(l("date"))))
    new CalendarDates(l)
  }

  val dateFormatter = DateTimeFormat.forPattern("yyyyMMdd")

  def dateFromString(s: String) = {
    LocalDate.parse(s, dateFormatter)
  }
}