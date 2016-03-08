package ch.octo.cffpoc.stationboard

import ch.octo.cffpoc.stops.Stop
import org.joda.time.DateTime

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
   *
   * @param evt
   * @return
   */
  def +(evt: StationBoardEvent): StationBoard = StationBoard(stop, events.updated(evt.train.id, evt))

  def size = events.size

  def filter(f: (StationBoardEvent) => Boolean) = {
    StationBoard(stop, events.filter({ case (k, v) => f(v) }))
  }

  private def orderEvents: Seq[StationBoardEvent] = events
    .values
    .toList
    .sortBy(_.departureTimestamp.get.getMillis)

  def take(n: Int): StationBoard = StationBoard(stop, orderEvents.take(n).map(e => (e.train.id -> e)).toMap)

  def before(dtime: DateTime): StationBoard = {
    val t = dtime.plusMillis(1)
    filter(e => e.departureTimestamp.map(_.isBefore(t)).getOrElse(false))
  }
  def after(dtime: DateTime): StationBoard = {
    val t = dtime.minusMillis(1)

    filter(e => e.departureTimestamp.map(_.isAfter(t)).getOrElse(false))
  }

  override def toString = stop + "\n" +
    orderEvents
    .map(e => s"${e.timestamp}\t${e.departureTimestamp.get}\t${e.delayMinute.getOrElse("-")}\t${e.train.name}\t${e.train.lastStopName}")
    .mkString("\n")

}
