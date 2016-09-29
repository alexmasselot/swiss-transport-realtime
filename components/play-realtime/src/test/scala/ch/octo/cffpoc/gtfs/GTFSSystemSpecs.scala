package ch.octo.cffpoc.gtfs

import org.scalatest.{ FlatSpec, Matchers }

/**
 * Created by alex on 17/02/16.
 */
class GTFSSystemSpecs extends FlatSpec with Matchers {
  behavior of "GTFSSystem"

  it should "indexIt OK" in {
    val l: List[(String, Int)] = List(("bb", 2), ("x", 42), ("aa", 1))
    val m: Map[Int, String] = Map(1 -> "aa", 2 -> "bb", 42 -> "x")

    GTFSSystem.indexIt(l, { (x: (String, Int)) => x._2 }, { (x: (String, Int)) => x._1 }) should equal(m)

  }

  it should "indexIt Fail" in {
    val l: List[(String, Int)] = List(("bb", 2), ("x", 42), ("aa", 2))

    an[GTFSParsingException] should be thrownBy {
      GTFSSystem.indexIt(l, { (x: (String, Int)) => x._2 }, { (x: (String, Int)) => x._1 })
    }
  }

  it should "loadAgency" in {
    GTFSSystem.loadAgencies("src/test/resources/gtfs").size shouldBe (410)
  }
  it should "loadStops" in {
    GTFSSystem.loadStops("src/test/resources/gtfs").size shouldBe (4480)
  }

  lazy val system = GTFSSystem.load("src/test/resources/gtfs")

  it should "load" in {
    val cd = system
  }

  it should "countTrips" in {
    system.countTrips should be(4)
  }


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
