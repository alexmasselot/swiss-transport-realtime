package ch.octo.cffpoc.stationboard

import ch.octo.cffpoc.DateMatchers
import ch.octo.cffpoc.streaming.StationBoardEventDecoder
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.scalatest.{ FlatSpec, Matchers }

import scala.io.Source

/**
 * Created by alex on 17/02/16.
 */
class StationBoardSpecs extends FlatSpec with Matchers with DateMatchers {
  behavior of "StationBoardEvent"

  val decoder = new StationBoardEventDecoder()

  def allEvents: Iterator[StationBoardEvent] = {
    Source.fromFile("src/test/resources/stationboards-laus-gva.jsonl")
      .getLines()
      .map(l => decoder.fromBytes(l.getBytes))
  }

  def gva2840: StationBoard = {
    val stop = allEvents.filter(e => e.stop.name == "Genève").next().stop
    val tmax = DateTime.parse("2016-02-29T18:40:00+0100")
    allEvents
      .filter(e => e.stop.name == "Genève" && e.timestamp.isBefore(tmax))
      .foldLeft(StationBoard(new DateTime(0L), stop))((acc, evt) => acc + evt)
  }

  def gva = allEvents.filter(e => e.stop.name == "Genève").next().stop

  it should "mocks size" in {
    allEvents.size should be(1139)
  }

  it should "original timestamp" in {
    assertDateEquals(gva2840.timestamp, DateTime.parse("2016-02-29T17:55:55.327+01:00"))
  }

  it should "size" in {
    val board = gva2840
    board.size should be(104)
    //println(board)
  }

  it should "filter(_.delayMinute.isDefined)" in {
    val board = gva2840.filter(_.delayMinute.isDefined)
    board.size should be(11)
  }

  it should "take(20)" in {
    val board = gva2840.take(10)
    board.size should be(10)
  }

  it should "before(...)" in {
    val board = gva2840.before(DateTime.parse("2016-02-29T17:42:00.000+01:00"))
    board.size should be(21)
  }

  it should "after(...)" in {
    val board = gva2840.after(DateTime.parse("2016-02-29T17:42:00.000+01:00"))
    board.size should be(104 - 21 + 2)
  }

  it should "stats" in {
    val board = gva2840.before(DateTime.parse("2016-02-29T17:42:00.000+01:00"))
    val stats = board.stats
    stats.stop should equal(gva)
    stats.timestamp.getMillis should equal(DateTime.parse("2016-02-29T17:55:55.327+01:00").getMillis)
    stats.total should equal(21)
    stats.delayed should equal(7)
  }

}
