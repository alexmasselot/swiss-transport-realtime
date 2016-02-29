package ch.octo.cffpoc.models

/**
 * Created by alex on 29/02/16.
 */
package object models {

  case class TimedPosition(
    timestamp: Long,
    position: GeoLoc)

}
