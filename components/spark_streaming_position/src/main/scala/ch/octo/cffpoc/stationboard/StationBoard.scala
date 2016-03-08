package ch.octo.cffpoc.stationboard

import ch.octo.cffpoc.stops.Stop

/**
 * This is one station board, with a list of StationBoardEvent.
 * It corresponds to the board w/arrivals+departures on the station hall
 *
 * Created by alex on 08/03/16.
 */
case class StationBoard(stop: Stop, events: Map[String, StationBoardEvent] = Map()) {

  /**
   * add a StationBoardEvent, thus a line.
   * It can replace an earlier one with the same train
   * @param evt
   * @return
   */
  def +(evt: StationBoardEvent): StationBoard = StationBoard(stop, events.updated(evt.train.id, evt))

  def size = events.size

  override def toString = stop + "\n" + events
    .values
    .toList
    .sortBy(_.departureTimestamp.get.getMillis)
    .map(e => s"${e.departureTimestamp.get}\t${e.delayMinute.getOrElse("-")}\t${e.train.name}\t${e.train.lastStopName}")
    .mkString("\n")

}
