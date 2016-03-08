package ch.octo.cffpoc.models

import ch.octo.cffpoc.stops.StopCloser
import org.joda.time.DateTime

/**
 * Created by alex on 23/02/16.
 */
case class TrainPositionSnapshot(timestamp: DateTime, positions: Seq[TrainPosition]) {

  def closedBy(closer: StopCloser) =
    TrainPositionSnapshot(
      timestamp,
      positions.map({
        p =>
          p.timedPosition match {
            case t: TimedPositionIsMoving if !t.moving => p.at(TimedPositionWithStop(
              p.timedPosition.timestamp,
              p.timedPosition.position,
              closer.findWithin(p.timedPosition.position)
            ))
            case _ => p
          }

      })
    )

  override def toString = s"stime=$timestamp\n" + positions.mkString("\n")
}
