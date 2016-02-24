package ch.octo.cffpoc

import ch.octo.cffpoc.streaming.serializers
import org.scalatest.{ FlatSpec, Matchers }

/**
 * Created by Alexandre Masselot on 17/02/16.
 * © OCTO Technology
 */
class TrainCFFPositionSpecs extends FlatSpec with Matchers {
  behavior of "TrainCFFPosition"

  val tcff = TrainCFFPosition(
    current = TrainPosition(
      trainid = "1",
      name = "x1",
      category = "X",
      lastStopName = "Calgary",
      timedPosition = TimedPosition(
        timestamp = 100,
        position = GeoLoc(10, 100)
      )
    ),
    futurePositions = List(
      TimedPosition(100, GeoLoc(10, 100)),
      TimedPosition(110, GeoLoc(11, 101)),
      TimedPosition(120, GeoLoc(12, 102)),
      TimedPosition(130, GeoLoc(13, 103))
    )
  )

  def check(at: Long, eT: Long, eLat: Double, eLng: Double) = {
    val t = tcff.at(at)
    t.trainid should equal("1")
    t.timedPosition.position should equal(GeoLoc(eLat, eLng))
    t.timedPosition.timestamp should equal(eT)
  }

  it should "< before" in {
    check(99, 100, 10, 100)
  }
  it should "<= before" in {
    check(100, 100, 10, 100)
  }
  it should "middle intevarl" in {
    check(115, 110, 11, 101)
  }

  it should "interval bound intevarl" in {
    check(110, 110, 11, 101)
  }

  it should "== last" in {
    check(130, 130, 13, 103)
  }

  it should "> last" in {
    check(131, 130, 13, 103)
  }

}
