package ch.octo.cffpoc

import org.joda.time.{ LocalDate, LocalTime, DateTime }

/**
 * Created by alex on 29/04/16.
 */
package object gtfs {
  case class ServiceId(value: String)
  case class RouteId(value: String)
  case class TripId(value: String)
  case class TripShortName(value: String)
  case class StopName(value: String)
  case class StopId(value: String)

  case class RawCalendarDate(serviceId: ServiceId, date: LocalDate)

  case class RawTrip(routeId: RouteId, serviceId: ServiceId, tripId: TripId, tripHeadSign: StopName, tripShortName: TripShortName)

  case class RawSTop(stopId: StopId, stopName: StopName, lat: Double, lng: Double)

  case class RawStopTime(tripId: TripId, stopId: StopId, timeArrival: LocalTime, timeDeparture: LocalTime)

}
