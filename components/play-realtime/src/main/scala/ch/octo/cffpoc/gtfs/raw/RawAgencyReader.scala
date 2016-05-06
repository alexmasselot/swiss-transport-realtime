package ch.octo.cffpoc.gtfs.raw

import ch.octo.cffpoc.gtfs._

/**
 * Created by alex on 03/05/16.
 */
object RawAgencyReader extends RawDataCollectionReader[RawAgency] {

  override def unmap(row: Map[String, String]): RawAgency = RawAgency(
    AgencyId(row("agency_id")),
    AgencyName(row("agency_name"))
  )
}
