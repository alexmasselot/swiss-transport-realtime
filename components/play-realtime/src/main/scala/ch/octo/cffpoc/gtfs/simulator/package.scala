package ch.octo.cffpoc.gtfs

import ch.octo.cffpoc.position.TimedPosition
import org.joda.time.DateTime

/**
  * Created by alex on 06.10.16.
  */
package object simulator {

  case class SimulatedPosition(secondsOfDay: Int,
                               lat: Double,
                               lng: Double,
                               tripId: TripId,
                               agencyId: AgencyId,
                               routeShortName: RouteShortName,
                               stopId: Option[StopId]
                              )

}
