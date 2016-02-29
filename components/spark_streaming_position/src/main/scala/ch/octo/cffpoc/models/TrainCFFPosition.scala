package ch.octo.cffpoc.models

import ch.octo.cffpoc.models.models.TimedPosition

/**
 * Created by Alexandre Masselot on 19/02/16.
 * © OCTO Technology
 */
case class TrainCFFPosition(
    current: TrainPosition,
    futurePositions: List[TimedPosition]) {
  val trainid = current.trainid
  val timeStamp = current.timedPosition.timestamp

  /**
   * get the latest train position for a given time,
   * picking into the future positions
   * The strategy is:
   * a) take the latest known position before the queried time stamp
   * b) if the queried timestamp is before any known position, returns the first one
   *
   * @param time the target time
   * @return an approximate position at the given time
   */
  def at(time: Long): TrainPosition = {
    futurePositions.takeWhile(_.timestamp <= time).lastOption match {
      case Some(p) => current.at(p)
      case None => current
    }
  }
}

