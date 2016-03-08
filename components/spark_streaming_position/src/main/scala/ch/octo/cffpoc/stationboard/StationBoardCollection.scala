package ch.octo.cffpoc.stationboard

import ch.octo.cffpoc.stops.Stop
import org.joda.time.DateTime

/**
 * a list of StationBoard's, mapped by the Stop id
 *
 * Created by alex on 08/03/16.
 */
case class StationBoardCollection(boards: Map[Long, StationBoard] = Map()) {
  /**
   * add as station board event
   *
   * @param evt
   * @return
   */
  def +(evt: StationBoardEvent): StationBoardCollection = {
    val board = boards.getOrElse(evt.stop.id, StationBoard(evt.stop))
    StationBoardCollection(boards.updated(evt.stop.id, board + evt))
  }

  def apply(id: Long) = boards(id)

  def size = boards.size

  def map(f: (StationBoard) => StationBoard) = StationBoardCollection(boards.map(e => (e._1 -> f(e._2))))

  def take(n: Int): StationBoardCollection = map(_.take(n))

  def filter(f: (StationBoardEvent) => Boolean): StationBoardCollection = map(_.filter(f))

  def before(dtime: DateTime): StationBoardCollection = map(_.before(dtime))

  def after(dtime: DateTime): StationBoardCollection = map(_.after(dtime))

}
