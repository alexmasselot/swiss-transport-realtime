package ch.octo.cffpoc.gtfs

import org.scalatest.{ FlatSpec, Matchers }

/**
 * Created by alex on 17/02/16.
 */
class GTFSSystemSpecs extends FlatSpec with Matchers {
  behavior of "CalendarDates"
  //  def load = GTFSSystem.load("src/test/resources/gtfs/calendar_dates.txt")
  //
  //  it should "load" in {
  //    val cd = load
  //  }
  //
  //  it should "countServiceId" in {
  //    load.countDates should equal(105)
  //  }
  //
  //  it should "countExceptions" in {
  //    load.countExceptions(CalendarDates.dateFromString("20160903")) should equal(2)
  //  }
  //
  //  it should "parseDate" in {
  //    val d = CalendarDates.dateFromString("20160116")
  //    d.getDayOfMonth should equal(16)
  //    d.getMonthOfYear should equal(1)
  //    d.getYear should equal(2016)
  //  }
  //
  //  it should "isRunning -> false" in {
  //    load.isRunning(ServiceId("3369:3:3:s"), CalendarDates.dateFromString("20160116")) should equal(false)
  //  }
  //  it should "isRunning serviceId known, but not the date" in {
  //    load.isRunning(ServiceId("3369:3:3:s"), CalendarDates.dateFromString("20161216")) should equal(true)
  //  }
  //  it should "isRunning serviceId unknown" in {
  //    load.isRunning(ServiceId("4242:3:3:s"), CalendarDates.dateFromString("20161216")) should equal(true)
  //  }
}
