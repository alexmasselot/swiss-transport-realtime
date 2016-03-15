package ch.octo.cffpoc.models

import ch.octo.cffpoc.DateMatchers
import ch.octo.cffpoc.position.{ TimedPosition, TrainPositionSnapshot, TrainPosition }
import org.joda.time.DateTime
import org.scalatest.{ FlatSpec, Matchers }

/**
 * Created by alex on 17/02/16.
 */
class TrainPositionSnapshotSpecs extends FlatSpec with Matchers with DateMatchers {
  behavior of "TrainPositionSnapshot"

  def mockDate(time: String): DateTime = {
    DateTime.parse(s"2016-02-29T$time.000+01:00")
  }

  val re = """(.*)/(.*)/(.*)""".r

  def mockTrain(str: String): TrainPosition = {
    val re(id, name, time) = str
    TrainPosition(
      train = Train(id = id, name = name, category = "X", lastStopName = "Calgary"),
      timedPosition = TimedPosition(timestamp = mockDate(time), position = GeoLoc(0, 0))
    )
  }

  def mockSnaphsot(str: String): TrainPositionSnapshot = {
    TrainPositionSnapshot(str.split("\n").toIterator.map(mockTrain).toList)
  }

  val snap_1 = mockSnaphsot(
    """1/x1/12:42:00
      |2/x2/12:43:00
      |3/x1/12:44:00
      |4/x1/12:45:00""".stripMargin)

  val snap_2 = mockSnaphsot(
    """1/x1/12:42:00
      |2/x2/12:43:00
      |1/x1/12:44:00
      |4/x1/12:45:00""".stripMargin)

  it should "mock works" in {
    val t2 = mockTrain("1/x1/12:42:00").at(TimedPosition(mockDate("12:54:00"), GeoLoc(12, 102)))
    t2.train.id should equal("1")
    t2.timedPosition should equal(TimedPosition(mockDate("12:54:00"), GeoLoc(12, 102)))
  }

  it should "size with no train dup" in {
    snap_1.size should be(4)
  }

  it should "size with  train dup" in {
    snap_2.size should be(3)
  }

  it should "timestamp" in {
    assertDateEquals(mockDate("12:45:00"), snap_2.timestamp)
  }
}
