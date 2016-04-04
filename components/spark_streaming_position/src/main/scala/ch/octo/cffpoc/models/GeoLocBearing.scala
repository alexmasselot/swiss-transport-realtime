package ch.octo.cffpoc.models

/**
 * Created by alex on 04/04/16.
 */
class GeoLocBearing(_lat: Double, _lng: Double, val bearing: Double) extends GeoLoc(_lat, _lng) {

  override def equals(other: Any) = other match {
    case ogl: GeoLocBearing => lat == ogl.lat && lng == ogl.lng && bearing == ogl.bearing
    case _ => false
  }

  override def toString = s"($lat, $lng) ↑$bearing"
}

object GeoLocBearing {
  def apply(lat: Double, lng: Double, bearing: Double) = new GeoLocBearing(lat, lng, bearing)
}