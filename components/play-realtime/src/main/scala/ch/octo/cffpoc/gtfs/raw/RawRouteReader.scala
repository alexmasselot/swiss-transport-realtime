package ch.octo.cffpoc.gtfs.raw

import ch.octo.cffpoc.gtfs._

/**
 * Created by alex on 03/05/16.
 */
object RawRouteReader extends RawDataCollectionReader[RawRoute] {

  override def unmap(row: Map[String, String]): RawRoute = RawRoute(
    RouteId(row("route_id")),
    AgencyId(row("agency_id")),
    RouteShortName(row("route_short_name")),
    RouteLongName(row("route_long_name"))
  )
}
