package ch.octo.cffpoc.models

/**
 * Created by alex on 29/02/16.
 */
case class GeoLoc(lat: Double, lng: Double) {
  val earthRadius = 6371000

  /**
   * distance in meters with another location
   * @param other
   * @return
   */
  def distanceMeters(other: GeoLoc): Double = {
    val dLat = Math.toRadians(other.lat - lat)
    val dLng = Math.toRadians(other.lng - lng)
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(other.lat)) *
      Math.sin(dLng / 2) * Math.sin(dLng / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    earthRadius * c
  }
}
