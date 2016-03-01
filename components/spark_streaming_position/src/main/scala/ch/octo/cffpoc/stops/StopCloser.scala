package ch.octo.cffpoc.stops

import ch.octo.cffpoc.models.GeoLoc

/**
 * Created by alex on 29/02/16.
 */
class StopCloser(stops: StopCollection, approxDistance: Double) {
  def findWithin(loc: GeoLoc): Option[Stop] =
    stops.toList
      .filter(_.position.distanceMeters(loc) <= approxDistance)
      .sortBy(_.position.distanceMeters(loc))
      .headOption
}
