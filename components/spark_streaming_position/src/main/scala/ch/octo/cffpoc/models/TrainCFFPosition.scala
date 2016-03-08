package ch.octo.cffpoc.models

/**
 * Created by Alexandre Masselot on 19/02/16.
 * © OCTO Technology
 */
case class TrainCFFPosition(
    current: TrainPosition,
    futurePositions: List[TimedPosition]) {
  val trainid = current.train.id
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
    futurePositions.zip(futurePositions.tail :+ TimedPosition(Long.MaxValue, GeoLoc(0, 0)))
      .takeWhile(_._1.timestamp <= time).lastOption match {
        case Some((p1, p2)) => current.at(TimedPositionIsMoving(p1.timestamp, p1.position, p1.position != p2.position))
        case None => current.at(TimedPositionIsMoving(current.timedPosition.timestamp, current.timedPosition.position, true))
      }
  }
}

