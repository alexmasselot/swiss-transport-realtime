package ch.octo.cffpoc.stops

import ch.octo.cffpoc.models.{ HasPosition, GeoLoc }

/**
 * Created by alex on 29/02/16.
 */
class StopCloser(stops: StopCollection, approxDistance: Double) {
  def findWithin(loc: HasPosition): Option[Stop] =
    stops.toList
      .filter(_.location.distanceMeters(loc) <= approxDistance)
      .sortBy(_.location.distanceMeters(loc))
      .headOption
}
