package ch.octo.cffpoc.stops

import ch.octo.cffpoc.models.GeoLoc
import org.scalatest.{ FlatSpec, Matchers }

/**
 * Created by alex on 17/02/16.
 */
class StopCloserSpecs extends FlatSpec with Matchers {
  behavior of "StopCloser"

  val closer = new StopCloser(StopCollection.load("src/test/resources/stops.txt"), 500)

  def check(lat: Double, lng: Double, exp: String) = {

    val oStop = closer.findWithin(GeoLoc(lat, lng))
    if (exp == "") {
      oStop should be(None)
    } else {
      oStop.map(_.name) should equal(Some(exp))
    }

  }

  check(47.391356, 8.051253, "Aarau")
  check(40.391356, 3.551253, "")
  check(47.395, 8.05, "Aarau")
  check(47.4, 8.05, "")
}
