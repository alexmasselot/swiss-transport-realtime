package ch.octo.cffpoc.position

import ch.octo.cffpoc.stops.StopCloser
import org.joda.time.DateTime

/**
 * Created by alex on 23/02/16.
 */
case class TrainPositionSnapshot(timestamp: DateTime = DateTime.now(), positions: Map[String, TrainPosition] = Map()) {

  def size = positions.size

  def closedBy(closer: StopCloser) =
    TrainPositionSnapshot(
      timestamp,
      positions.map({
        case (key, p) =>
          p.timedPosition match {
            case t: TimedPositionIsMoving if !t.moving =>
              (key -> p.at(TimedPositionWithStop(
                p.timedPosition.timestamp,
                p.timedPosition.position,
                closer.findWithin(p.timedPosition.position)
              )))
            case _ => (key -> p)
          }

      })
    )

  /**
   * add a train position. the timesatamp will be from this later insert (no check on date order, as they ultimately should arrive intimely fashion.
   * The positions map is update with the train id
   *
   * @param tp the train position
   * @return a new TrainPositionSnapshot
   */
  def +(tp: TrainPosition): TrainPositionSnapshot = TrainPositionSnapshot(tp.timedPosition.timestamp, positions.updated(tp.train.id, tp))

  override def toString = s"stime=$timestamp\n" + positions.mkString("\n")
}

object TrainPositionSnapshot {
  def apply(positions: Seq[TrainPosition]): TrainPositionSnapshot =
    positions.foldLeft(new TrainPositionSnapshot())((acc, tp) => acc + tp)

  def apply(timestamp: DateTime, positions: Seq[TrainPosition]): TrainPositionSnapshot =
    new TrainPositionSnapshot(timestamp, positions.map(tp => (tp.train.id -> tp)).toMap)

}
