package ch.octo

/**
 * Created by Alexandre Masselot on 17/02/16.
 * © OCTO Technology
 */
package object cffpoc {

  case class GeoLoc(lat: Double, lng: Double)

  case class TimedPosition(
    timestamp: Long,
    position: GeoLoc)

}
