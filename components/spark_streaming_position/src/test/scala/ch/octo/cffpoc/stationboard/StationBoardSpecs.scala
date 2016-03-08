package ch.octo.cffpoc.stationboard

import ch.octo.cffpoc.models.GeoLoc
import ch.octo.cffpoc.stops.Stop
import org.joda.time.DateTime
import org.scalatest.{ FlatSpec, Matchers }

import scala.concurrent.duration._
import scala.io.Source

/**
 * Created by alex on 17/02/16.
 */
class StationBoardSpecs extends FlatSpec with Matchers {
  behavior of "StationBoardEvent"

  val decoder = new StationBoardEventDecoder()

  def allEvents: Iterator[StationBoardEvent] = {
    Source.fromFile("src/test/resources/stationboards-laus-gva.jsonl")
      .getLines()
      .map(l => decoder.fromBytes(l.getBytes))
  }

  it should "mocks size" in {
    allEvents.size should be(1223)
  }

  it should "pouet" in {
    val tmax = DateTime.parse("2016-02-29T18:40:00+0100")
    val tbound = DateTime.parse("2016-02-29T18:13:57.471+01:00")

    val stop = allEvents.filter(e => e.stop.name == "Genève").next().stop
    allEvents.filter(e => e.stop.name == "Genève").foreach(println)

    val board: StationBoard = allEvents
      .filter(e => e.stop.name == "Genève" && e.timestamp.isBefore(tmax))
      .foldLeft(StationBoard(stop))((acc, evt) => acc + evt)
    println(board)
  }
}
