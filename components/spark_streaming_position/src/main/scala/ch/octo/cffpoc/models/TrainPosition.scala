package ch.octo.cffpoc.models

/**
 * Created by alex on 19/02/16.
 */

case class TrainPosition(
    trainid: String,
    category: String,
    name: String,
    timedPosition: HasTimedPosition,
    lastStopName: String) {
  /**
   * instanciate a train copy with another time & position
   *
   * @param newPosition the new time & position
   * @return
   */
  def at(newPosition: HasTimedPosition): TrainPosition = TrainPosition(
    trainid = trainid,
    category = category,
    name = name,
    lastStopName = lastStopName,
    timedPosition = newPosition
  )

  override def toString = {
    val loc = timedPosition match {
      case TimedPositionWithStop(_, _, Some(c)) => c.name
      case _ => "-"
    }
    s"$trainid\t$category\t$loc\t${timedPosition.timestamp}\t${timedPosition.position.lat}\t${timedPosition.position.lng}\t$name\t$lastStopName"
  }
}

