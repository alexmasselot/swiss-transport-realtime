package ch.octo.cffpoc.gtfs

/**
 * Created by alex on 03/09/16.
 */
class TripCollection(trips: Map[TripId, Trip]) {
  def size = trips.size
  def apply(tripId: TripId) = trips(tripId)
  def toList = trips.values.toList
}

object TripCollection {
  def apply(trips: Map[TripId, Trip]) = new TripCollection(trips)
}
