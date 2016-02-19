package ch.octo.cffpoc.streaming

import ch.octo.cffpoc.{ TrainPosition, GeoLoc, TrainCFFPosition }
import org.scalatest.{ FlatSpec, Matchers }

/**
 * Created by alex on 17/02/16.
 */
class TrainCFFPositionLatestCollectionSpecs extends FlatSpec with Matchers {

  implicit def convTrainPosition(p: (String, Int)): TrainCFFPosition = TrainCFFPosition(
    current = TrainPosition(
      trainid = p._1,
      name = s"IC${p._1}",
      category = "X",
      lastStopName = "Calgary",
      timeStamp = p._2,
      location = GeoLoc(1, 2)
    ),
    futurePositions = List()
  )

  behavior of "TrainCFFPositionLatestCollection"

  it should "empty" in {
    val m = TrainCFFPositionLatestCollection()
    m.size should equal(0)
    m.toList should equal(List())
  }

  //beware of implicit convertione!!!
  it should "two different elements" in {
    val m = TrainCFFPositionLatestCollection() +
      ("a", 10) +
      ("b", 11)

    m.size should equal(2)
    m.toList.sortBy(_.trainid) should equal(List[TrainCFFPosition](("a", 10), ("b", 11)))
  }
  it should "three with 2 same id, increasing timeStamps" in {
    val m = TrainCFFPositionLatestCollection() +
      ("a", 10) +
      ("b", 11) +
      ("a", 13)

    m.size should equal(2)
    m.toList.sortBy(_.trainid) should equal(List[TrainCFFPosition](("a", 13), ("b", 11)))
  }
  it should "three with 2 same id, decreasing timeStamps" in {
    val m = TrainCFFPositionLatestCollection() +
      ("a", 10) +
      ("b", 11) +
      ("a", 7)

    m.size should equal(2)
    m.toList.sortBy(_.trainid) should equal(List[TrainCFFPosition](("a", 10), ("b", 11)))
  }

  it should "crete with a list" in {
    val m = TrainCFFPositionLatestCollection() +
      List[TrainCFFPosition](
        ("a", 10),
        ("b", 11),
        ("a", 13)
      )

    m.size should equal(2)
    m.toList.sortBy(_.trainid) should equal(List[TrainCFFPosition](("a", 13), ("b", 11)))
  }

  it should "merge 2 list" in {
    val m1 = TrainCFFPositionLatestCollection() +
      ("a", 10) +
      ("b", 11) +
      ("c", 7)

    val m2 = TrainCFFPositionLatestCollection() +
      ("a", 7) +
      ("b", 15) +
      ("d", 7)

    (m1 + m2).toList.sortBy(_.trainid) should equal(List[TrainCFFPosition](
      ("a", 10),
      ("b", 15),
      ("c", 7),
      ("d", 7)
    ))
  }

}
