package ch.octo.cffpoc.models

/**
 * Created by alex on 23/02/16.
 */
case class TrainPositionSnapshot(timestamp: Long, positions: Seq[TrainPosition]) {
  override def toString = s"stime=$timestamp\n" + positions.mkString("\n")
}
