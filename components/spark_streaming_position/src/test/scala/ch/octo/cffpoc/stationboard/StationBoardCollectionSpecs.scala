package ch.octo.cffpoc.stationboard

import org.joda.time.DateTime
import org.scalatest.{ FlatSpec, Matchers }

import scala.io.Source

/**
 * Created by alex on 17/02/16.
 */
class StationBoardCollectionSpecs extends FlatSpec with Matchers {
  behavior of "StationBoardCollection"

  val decoder = new StationBoardEventDecoder()

  def allEvents: Iterator[StationBoardEvent] = {
    Source.fromFile("src/test/resources/stationboards-laus-gva.jsonl")
      .getLines()
      .map(l => decoder.fromBytes(l.getBytes))
  }

  val gva = allEvents.filter(e => e.stop.name == "Genève").next().stop

  def boards = allEvents.foldLeft(StationBoardCollection())({ (acc, evt) => acc + evt })

  it should "size" in {
    boards.size should be(5)
  }

  it should "filter(_.delayMinute.isDefined)" in {
    val fBoards = boards.filter(_.delayMinute.isDefined)
    fBoards.size should be(5)
    fBoards(8501008L).size should be(11)
  }

  it should "take(20)" in {
    val fBoards = boards.take(10)
    fBoards.size should be(5)
    fBoards(8501008L).size should be(10)
  }

  it should "before(...)" in {
    val fBoards = boards.before(DateTime.parse("2016-02-29T17:42:00.000+01:00"))
    fBoards.size should be(5)
    fBoards(8501008L).size should be(21)
  }

  it should "after(...)" in {
    val fBoards = boards.after(DateTime.parse("2016-02-29T17:42:00.000+01:00"))
    fBoards.size should be(5)
    fBoards(8501008L).size should be(104 - 21 + 2)
  }
}
