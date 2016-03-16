package ch.octo.cffpoc.stationboard

import org.joda.time.DateTime

/**
 * a list of StationBoard's, mapped by the Stop id
 * at a given time
 *
 * Created by alex on 08/03/16.
 */
case class StationBoardsSnapshot(timestamp: DateTime = new DateTime(0L), boards: Map[Long, StationBoard] = Map()) {
  /**
   * add as station board event
   *
   * @param evt
   * @return
   */
  def +(evt: StationBoardEvent): StationBoardsSnapshot = {
    val board = boards.getOrElse(evt.stop.id, StationBoard(evt.timestamp, evt.stop))
    StationBoardsSnapshot(evt.timestamp, boards.updated(evt.stop.id, board + evt))
  }

  def get(id: Long): Option[StationBoard] = boards.get(id)

  def get(stopName: String): Option[StationBoard] = boards.values.find(_.stop.name == stopName)

  def apply(id: Long) = boards(id)

  def size = boards.size

  def countAll = boards.values.map(_.size).sum

  def map(f: (StationBoard) => StationBoard) = {
    val mBoards = boards.map(e => (e._1 -> f(e._2)))
    val t = if (mBoards.size == 0) DateTime.now else mBoards.values.map(_.timestamp).maxBy(_.getMillis)
    StationBoardsSnapshot(t, mBoards)
  }

  def take(n: Int): StationBoardsSnapshot = map(_.take(n))

  def filter(f: (StationBoardEvent) => Boolean): StationBoardsSnapshot = map(_.filter(f))

  def before(dtime: DateTime): StationBoardsSnapshot = map(_.before(dtime))

  def after(dtime: DateTime): StationBoardsSnapshot = map(_.after(dtime))

  override def toString = {
    "board at " + timestamp + "\n" +
      boards.values.toList.sortBy(_.stop.name)
      .map(_.toString).mkString("\n\n")
  }

  def summary = {
    s"${size} boards, ${countAll} lines"
  }

}
