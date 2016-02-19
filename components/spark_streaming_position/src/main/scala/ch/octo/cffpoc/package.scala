package ch.octo

/**
 * Created by alex on 17/02/16.
 */
package object cffpoc {
  case class GeoLoc(lat: Double, lng: Double)

  case class TimedPosition(
    timeStamp: Long,
    location: GeoLoc)

  case class TrainPosition(trainid: String,
    category: String,
    name: String,
    timeStamp: Long,
    lastStopName: String,
    location: GeoLoc)

  case class TrainCFFPosition(current: TrainPosition,
      futurePositions: List[TimedPosition]) {
    val trainid = current.trainid
  }

}
