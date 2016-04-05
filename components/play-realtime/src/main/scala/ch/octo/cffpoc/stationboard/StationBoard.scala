package ch.octo.cffpoc.stationboard

import ch.octo.cffpoc.stops.Stop
import org.joda.time.DateTime

/**
 * This is one station board, with a list of StationBoardEvent.
 * It corresponds to the board w/arrivals+departures on the station hall
 *
 * Created by alex on 08/03/16.
 */
case class StationBoard(timestamp: DateTime, stop: Stop, events: Map[String, StationBoardEvent] = Map()) {

  /**
   * add a StationBoardEvent, thus a line.
   * It can replace an earlier one with the same train
   *
   * @param evt
   * @return
   */
  def +(evt: StationBoardEvent): StationBoard = StationBoard(evt.timestamp, stop, events.updated(evt.train.id, evt))

  def size = events.size

  def filter(f: (StationBoardEvent) => Boolean) = {
    val fevts = events.filter({ case (k, v) => f(v) })

    val t = if (fevts.isEmpty) DateTime.now() else fevts.values.map(_.timestamp).maxBy(_.getMillis)
    StationBoard(t, stop, fevts)
  }

  private def orderEvents: Seq[StationBoardEvent] = events
    .values
    .toList
    .sortBy(_.departureTimestamp.get.getMillis)

  def take(n: Int): StationBoard = {
    val evts = orderEvents.take(n)
    val t = evts.lastOption.map(_.timestamp).getOrElse(DateTime.now())
    StationBoard(t, stop, evts.map(e => (e.train.id -> e)).toMap)
  }

  def before(dtime: DateTime): StationBoard = {
    val t = dtime.plusMillis(1)
    filter(e => e.departureTimestamp.map(_.isBefore(t)).getOrElse(false))
  }

  /**
   * is the event still relevant after a given date?
   * noe if the departure + delay is after it
   *
   * @param dtime
   * @return
   */
  def after(dtime: DateTime): StationBoard = {
    val t = dtime.minusMillis(1)

    filter(e => (e.departureTimestamp, e.delayMinute) match {
      case (None, _) => false
      case (Some(dt), None) => dt.isAfter(t)
      case (Some(dt), Some(dd)) => dt.isAfter(t.plusMinutes(dd))
    })
  }

  def stats: StationBoardStats = StationBoardStats(
    timestamp,
    stop,
    size,
    events.filter({ case (k, v) => v.delayMinute.isDefined }).size
  )

  override def toString = s"$stop @$timestamp \n" +
    orderEvents
    .map(e => s"${e.timestamp}\t${e.departureTimestamp.get}\t${e.delayMinute.getOrElse("-")}\t${e.train.name}\t${e.train.lastStopName}")
    .mkString("\n")

}
//object StationBoard {
//  def apply(stop: Stop) = new StationBoard(DateTime.now(), stop)
//}