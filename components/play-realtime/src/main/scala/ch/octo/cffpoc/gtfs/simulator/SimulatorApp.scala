package ch.octo.cffpoc.gtfs.simulator

import ch.octo.cffpoc.gtfs.GTFSSystem
import ch.octo.cffpoc.gtfs.raw.RawCalendarDateReader
import org.apache.commons.logging.LogFactory

/**
  * Created by alex on 07.10.16.
  */
object SimulatorApp extends App {
  val LOGGER = LogFactory.getLog(SimulatorApp.getClass)

  lazy val system = GTFSSystem.load("src/main/resources/gtfs")
  val date = RawCalendarDateReader.dateFromString("20161007")
  LOGGER.info(s"findAllTripsByDate($date)")
  val trips = system.findAllTripsByDate(date)
  LOGGER.info(s"transforming ${trips.size} trips into simulated positions")

  val simPos = SimulatedTripPositions.merge(trips, date, 30, true)
  LOGGER.info(s"build a list of ${simPos.size} simulated positions")

}
