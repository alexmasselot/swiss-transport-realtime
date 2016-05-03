package ch.octo.cffpoc.gtfs.raw

import org.scalatest.{ FlatSpec, Matchers }

/**
 * Created by alex on 17/02/16.
 */
class StopTimesSpecs extends FlatSpec with Matchers {
  behavior of "CalendarDates"
  def load = StopTimes.load("src/test/resources/gtfs/stop_times.txt")

  it should "load" in {
    val cd = load
  }

  it should "size" in {
    load.size should equal(47)
  }

  it should "parseDate" in {
    val t = StopTimes.timeFromString("17:15:07")
    t.getMinuteOfHour should equal(15)
    t.getSecondOfMinute should equal(7)
    t.getHourOfDay should equal(17)
  }

}
