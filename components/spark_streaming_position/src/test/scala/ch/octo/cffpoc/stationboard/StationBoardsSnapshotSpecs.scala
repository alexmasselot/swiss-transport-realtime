package ch.octo.cffpoc.stationboard

import ch.octo.cffpoc.DateMatchers
import ch.octo.cffpoc.streaming.StationBoardEventDecoder
import org.joda.time.DateTime
import org.scalatest.{ FlatSpec, Matchers }

import scala.io.Source

/**
 *
 * Created by alex on 17/02/16.
 */
class StationBoardsSnapshotSpecs extends FlatSpec with Matchers with DateMatchers {
  behavior of "StationBoardsSnapshot"

  val decoder = new StationBoardEventDecoder()

  def allEvents: Iterator[StationBoardEvent] = {
    Source.fromFile("src/test/resources/stationboards-laus-gva.jsonl")
      .getLines()
      .map(l => decoder.fromBytes(l.getBytes))
  }

  val gva = allEvents.filter(e => e.stop.name == "Genève").next().stop

  def boards = allEvents.foldLeft(StationBoardsSnapshot())({ (acc, evt) => acc + evt })

  it should "size" in {
    boards.size should be(5)
  }

  it should "have timestamp with last" in {
    assertDateEquals(boards.timestamp, DateTime.parse("2016-02-29T17:56:32.320+01:00"))
  }

  it should "all from gva" in {
    val board = boards(8501008L)
    board.size should be(104)
  }

  it should "filter(_.delayMinute.isDefined)" in {
    val fBoards = boards.filter(_.delayMinute.isDefined)
    fBoards.size should be(5)
    fBoards(8501008L).size should be(11)
  }

  it should "take(10)" in {
    val fBoards = boards.take(10)
    fBoards.size should be(5)
    fBoards(8501008L).size should be(10)
    assertDateEquals(fBoards(8501008L).timestamp, DateTime.parse("2016-02-29T17:02:45.593+01:00"))
    assertDateEquals(fBoards.timestamp, DateTime.parse("2016-02-29T17:56:00.487+01:00"))
  }

  it should "before(17:42)" in {
    val fBoards = boards.before(DateTime.parse("2016-02-29T17:42:00.000+01:00"))
    fBoards.size should be(5)

    fBoards(8501008L).size should be(21)
    assertDateEquals(fBoards.timestamp, DateTime.parse("2016-02-29T17:55:55.327Z"))
  }

  it should "after(...)" in {
    val fBoards = boards.after(DateTime.parse("2016-02-29T17:42:00.000+01:00"))
    fBoards.size should be(5)

    assertDateEquals(fBoards.timestamp, DateTime.parse("2016-02-29T17:56:32.750Z"))
    fBoards(8501008L).size should be(104 - 21 + 2)
  }
}

