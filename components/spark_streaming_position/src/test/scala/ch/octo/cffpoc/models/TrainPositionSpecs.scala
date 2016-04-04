package ch.octo.cffpoc.models

import ch.octo.cffpoc.position.{ TimedPosition, TrainPosition }
import org.joda.time.DateTime
import org.scalatest.{ FlatSpec, Matchers }

/**
 * Created by alex on 17/02/16.
 */
class TrainPositionSpecs extends FlatSpec with Matchers {
  behavior of "TrainPosition"

  val trainPos = TrainPosition(
    train = Train(id = "1",
      name = "x1",
      category = "X",
      lastStopName = "Calgary"),
    timedPosition = TimedPosition(
      timestamp = new DateTime(100L),
      position = GeoLocBearing(10, 100, 40)
    )
  )

  it should "at" in {
    val t2 = trainPos.at(TimedPosition(new DateTime(200L), GeoLocBearing(12, 102, 60)))
    t2.train.id should equal("1")
    t2.timedPosition should equal(TimedPosition(new DateTime(200L), GeoLocBearing(12, 102, 60)))
  }

}
