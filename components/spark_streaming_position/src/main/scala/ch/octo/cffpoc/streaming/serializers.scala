package ch.octo.cffpoc.streaming

import ch.octo.cffpoc.{ TrainPositionSnapshot, TrainPosition, TimedPosition, GeoLoc }
import play.api.libs.json.Json

/**
 * Created by alex on 24/02/16.
 */
object serializers {
  implicit val formatGeoLoc = Json.format[GeoLoc]
  implicit val formatTimedPosition = Json.format[TimedPosition]
  implicit val formatTrainPosition = Json.format[TrainPosition]
  implicit val formatTrainPositionSnapshot = Json.format[TrainPositionSnapshot]

}
