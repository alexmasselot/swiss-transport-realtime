package ch.octo.cffpoc.stops

import org.scalatest.{ FlatSpec, Matchers }

/**
 * Created by alex on 17/02/16.
 */
class StopCollectionSpecs extends FlatSpec with Matchers {
  behavior of "TrainPosition"

  val stops = StopCollection.load("src/test/resources/stops.txt")
  it should "size" in {
    stops.size should be(1907)
  }
}
