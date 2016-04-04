package ch.octo.cffpoc.models

import ch.octo.cffpoc.position.{ TimedPosition, TimedPositionIsMoving, TrainCFFPosition, TrainPosition }
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
        position = GeoLocBearing(10, 100, 40)
      )
    ),
    futurePositions = List(
      TimedPosition(new DateTime(100L), GeoLocBearing(10, 100, 40)),
      TimedPosition(new DateTime(110L), GeoLocBearing(11, 101, 50)),
      TimedPosition(new DateTime(120L), GeoLocBearing(12, 102, 70)),
      TimedPosition(new DateTime(125L), GeoLocBearing(12, 102, 70)),
      TimedPosition(new DateTime(130L), GeoLocBearing(13, 103, 80))
    )
  )

  def check(at: Long, eT: Long, eLat: Double, eLng: Double, eBearing: Double, eMoving: Boolean) = {
    val t = tcff.at(new DateTime(at))
    t.train.id should equal("1")
    t.timedPosition.position should equal(GeoLocBearing(eLat, eLng, eBearing))
    t.timedPosition.timestamp should equal(new DateTime(eT))
    t.timedPosition.isInstanceOf[TimedPositionIsMoving] should be(true)
    t.timedPosition.asInstanceOf[TimedPositionIsMoving].moving should be(eMoving)
  }

  it should "< before" in {
    check(99, 100, 10, 100, 40, true)
  }
  it should "<= before" in {
    check(100, 100, 10, 100, 40, true)
  }
  it should "middle intevarl" in {
    check(115, 110, 11, 101, 50, true)
  }

  it should "interval bound intevarl" in {
    check(110, 110, 11, 101, 50, true)
  }

  it should "interval bound stop bef" in {
    check(120, 120, 12, 102, 70, false)
  }
  it should "interval bound stop end" in {
    check(125, 125, 12, 102, 70, true)
  }
  it should "== last" in {
    check(130, 130, 13, 103, 80, true)
  }

  it should "> last" in {
    check(131, 130, 13, 103, 80, true)
  }

  it should "latestTimestamp" in {
    tcff.latestTimestamp.getMillis should be(130L)
  }

  it should "isBefore yes" in {
    tcff.isBefore(new DateTime(140L)) should be(true)
  }

  it should "isBefore nope" in {
    tcff.isBefore(new DateTime(130L)) should be(false)
  }
  it should "isBefore equal" in {
    tcff.isBefore(new DateTime(125L)) should be(false)
  }
}
