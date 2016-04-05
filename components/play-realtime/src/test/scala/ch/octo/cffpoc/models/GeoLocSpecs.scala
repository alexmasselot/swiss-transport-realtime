package ch.octo.cffpoc.models

import org.scalatest.{ FlatSpec, Matchers }

/**
 * Created by alex on 17/02/16.
 */
class GeoLocSpecs extends FlatSpec with Matchers {
  behavior of "TrainPosition"

  def check(lat1: Double, long1: Double, lat2: Double, long2: Double, expectedDistance: Double) = {
    it should s"|($lat1, $long1), ($lat2, $long2)| ~ $expectedDistance" in {
      GeoLoc(lat1, long1).distanceMeters(GeoLoc(lat2, long2)) should equal(expectedDistance +- 100)
    }
  }

  check(0, 0, 0, 0, 0)
  check(180, 0, -180, 0, 0)
  check(38.898556, -77.037852, 38.897147, -77.043934, 549)
  check(38.898556, -77.037852, 38.897147, -35.043934, 3601072)
  check(-38.898556, -77.037852, 38.897147, -77.043934, 8650487)
  check(38.897147, -77.043934, -38.898556, -77.037852, 8650487)

}
