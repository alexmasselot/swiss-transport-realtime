package ch.octo.cffpoc.models

import org.joda.time.DateTime
import org.scalatest.{ FlatSpec, Matchers }

/**
 * Created by Alexandre Masselot on 17/02/16.
 * © OCTO Technology
 */
class TrainCFFPositionSpecs extends FlatSpec with Matchers {
  behavior of "TrainCFFPosition"

  val tcff = TrainCFFPosition(
    current = TrainPosition(
      train = Train(id = "1",
        name = "x1",
        category = "X",
        lastStopName = "Calgary"
      ),
      timedPosition = TimedPosition(
        timestamp = new DateTime(100L),
        position = GeoLoc(10, 100)
      )
    ),
    futurePositions = List(
      TimedPosition(new DateTime(100L), GeoLoc(10, 100)),
      TimedPosition(new DateTime(110L), GeoLoc(11, 101)),
      TimedPosition(new DateTime(120L), GeoLoc(12, 102)),
      TimedPosition(new DateTime(125L), GeoLoc(12, 102)),
      TimedPosition(new DateTime(130L), GeoLoc(13, 103))
    )
  )

  def check(at: Long, eT: Long, eLat: Double, eLng: Double, eMoving: Boolean) = {
    val t = tcff.at(new DateTime(at))
    t.train.id should equal("1")
    t.timedPosition.position should equal(GeoLoc(eLat, eLng))
    t.timedPosition.timestamp should equal(new DateTime(eT))
    t.timedPosition.isInstanceOf[TimedPositionIsMoving] should be(true)
    t.timedPosition.asInstanceOf[TimedPositionIsMoving].moving should be(eMoving)
  }

  it should "< before" in {
    check(99, 100, 10, 100, true)
  }
  it should "<= before" in {
    check(100, 100, 10, 100, true)
  }
  it should "middle intevarl" in {
    check(115, 110, 11, 101, true)
  }

  it should "interval bound intevarl" in {
    check(110, 110, 11, 101, true)
  }

  it should "interval bound stop bef" in {
    check(120, 120, 12, 102, false)
  }
  it should "interval bound stop end" in {
    check(125, 125, 12, 102, true)
  }
  it should "== last" in {
    check(130, 130, 13, 103, true)
  }

  it should "> last" in {
    check(131, 130, 13, 103, true)
  }

}
