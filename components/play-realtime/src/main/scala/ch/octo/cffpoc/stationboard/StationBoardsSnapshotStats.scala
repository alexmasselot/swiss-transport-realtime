package ch.octo.cffpoc.stationboard

import ch.octo.cffpoc.stops.Stop
import org.joda.time.DateTime

/**
 * Created by alex on 17/03/16.
 */
case class StationBoardsSnapshotStats(timestamp: DateTime = DateTime.now(),
    stats: Map[Long, StationBoardStats] = Map()) {

  def +(st: StationBoardStats) = StationBoardsSnapshotStats(st.timestamp, stats + (st.stop.id -> st))

  def total = stats.values.map(_.total).sum

  def delayed = stats.values.map(_.delayed).sum

  override def toString = s"stops=${stats.size} total=$total delayed=$delayed"
}