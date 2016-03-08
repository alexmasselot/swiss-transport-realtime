package ch.octo.cffpoc.streaming

import ch.octo.cffpoc.models.{ TrainPositionSnapshot, TrainCFFPosition }
import org.joda.time.DateTime

/**
 * we build a collection of train where there is only one trainid and we keep the latest train
 * Created by alex on 17/02/16.
 */
@SerialVersionUID(10001L)
class TrainCFFPositionLatestCollection(mCol: Map[String, TrainCFFPosition]) extends Serializable {

  /**
   * add a TrainCFFPosition and eventually replace an earlier train with same trainid
   *
   * @param p a new cff train position
   * @return a new collection
   */
  def +(p: TrainCFFPosition): TrainCFFPositionLatestCollection = mCol.get(p.trainid) match {
    case None => new TrainCFFPositionLatestCollection(mCol + (p.trainid -> p))
    case Some(x) if x.timeStamp.isBefore(p.timeStamp) => new TrainCFFPositionLatestCollection(mCol + (p.trainid -> p))
    case _ => this
  }

  /**
   * add a sequence of TrainCFFPosition and eventually replace an earlier train with same trainid
   *
   * @param ps a sequence of cff train position
   * @return a new collection
   */
  def +(ps: Seq[TrainCFFPosition]): TrainCFFPositionLatestCollection =
    ps.foldLeft(this)((acc: TrainCFFPositionLatestCollection, p: TrainCFFPosition) => acc + p)

  /**
   * add another TrainCFFPositionLatestCollection and eventually replace an earlier train with same trainid
   *
   * @param pcol another collection
   * @return a new collection
   */
  def +(pcol: TrainCFFPositionLatestCollection): TrainCFFPositionLatestCollection =
    pcol.toList.foldLeft(this)((acc: TrainCFFPositionLatestCollection, p: TrainCFFPosition) => acc + p)

  /**
   * number of elements
   *
   * @return
   */
  def size = mCol.size

  /**
   * get the list of TrainCFFPosition
   *
   * @return
   */
  def toList = mCol.values.toList

  /**
   * build a trainposition snapshot
   *
   * @param time a time with the approximative train positions
   * @return
   */
  def snapshot(time: DateTime): TrainPositionSnapshot = {
    TrainPositionSnapshot(time, toList.map(_.at(time)))
  }

  override def toString = mCol.values.map(p => s"${p.timeStamp}\t${p.current.train.id}").mkString("\n");
}

object TrainCFFPositionLatestCollection {
  def apply() = new TrainCFFPositionLatestCollection(Map())
}
