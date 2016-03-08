package ch.octo.cffpoc.models

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
      timestamp = 100,
      position = GeoLoc(10, 100)
    )
  )

  it should "at" in {
    val t2 = trainPos.at(TimedPosition(200, GeoLoc(12, 102)))
    t2.train.id should equal("1")
    t2.timedPosition should equal(TimedPosition(200, GeoLoc(12, 102)))
  }

}
