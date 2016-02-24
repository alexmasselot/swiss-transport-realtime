package ch.octo.cffpoc

/**
 * Created by alex on 19/02/16.
 */

case class TrainPosition(
    trainid: String,
    category: String,
    name: String,
    timedPosition: TimedPosition,
    lastStopName: String) {
  /**
   * instanciate a train copy with another time & position
   *
   * @param newPosition the new time & position
   * @return
   */
  def at(newPosition: TimedPosition): TrainPosition = TrainPosition(
    trainid = trainid,
    category = category,
    name = name,
    lastStopName = lastStopName,
    timedPosition = newPosition
  )

  override def toString = s"$trainid\t$category\t${timedPosition.timestamp}\t${timedPosition.position.lat}\t${timedPosition.position.lng}\t$name\t$lastStopName"
}

