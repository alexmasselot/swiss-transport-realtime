package ch.octo.cffpoc.position

import ch.octo.cffpoc.models.Train

/**
 * Created by alex on 19/02/16.
 */

case class TrainPosition(
    train: Train,
    timedPosition: HasTimedPosition) {
  /**
   * instanciate a train copy with another time & position
   *
   * @param newPosition the new time & position
   * @return
   */
  def at(newPosition: HasTimedPosition): TrainPosition = TrainPosition(
    train = train,
    timedPosition = newPosition
  )

  override def toString = {
    val loc = timedPosition match {
      case TimedPositionWithStop(_, _, Some(c)) => c.name
      case _ => "-"
    }
    s"${train.id}\t${train.category}\t$loc\t${timedPosition.timestamp}\t${timedPosition.position.lat}\t${timedPosition.position.lng}\t${train.name}\t${train.lastStopName}"
  }
}

