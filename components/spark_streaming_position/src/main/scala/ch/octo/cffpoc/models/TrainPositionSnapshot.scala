package ch.octo.cffpoc.models

import ch.octo.cffpoc.stops.StopCloser

/**
 * Created by alex on 23/02/16.
 */
case class TrainPositionSnapshot(timestamp: Long, positions: Seq[TrainPosition]) {

  def closedBy(closer: StopCloser) =
    TrainPositionSnapshot(
      timestamp,
      positions.map(p => p.at(TimedPositionWithStop(
        p.timedPosition.timestamp,
        p.timedPosition.position,
        closer.findWithin(p.timedPosition.position)
      )))
    )

  override def toString = s"stime=$timestamp\n" + positions.mkString("\n")
}
