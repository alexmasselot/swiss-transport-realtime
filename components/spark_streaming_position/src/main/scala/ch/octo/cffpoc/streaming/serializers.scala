package ch.octo.cffpoc.streaming

import ch.octo.cffpoc.models._
import ch.octo.cffpoc.stops.Stop
import play.api.libs.json._

/**
 * Created by alex on 24/02/16.
 */
object serializers {

  implicit val formatGeoLoc = Json.format[GeoLoc]
  implicit val formatStop = Json.format[Stop]

  implicit object formatHasTimedPosition extends Writes[HasTimedPosition] {
    override def writes(o: HasTimedPosition): JsValue =
      o match {
        case t: TimedPosition => Json.writes[TimedPosition].writes(t)
        case t: TimedPositionIsMoving => Json.writes[TimedPositionIsMoving].writes(t)
        case t: TimedPositionWithStop => Json.writes[TimedPositionWithStop].writes(t)
      }
  }

  implicit val formatTrain = Json.writes[Train]
  implicit val formatTrainPosition = Json.writes[TrainPosition]
  implicit val formatTrainPositionSnapshot = Json.writes[TrainPositionSnapshot]

  //  val hasTimedPositionReads: Reads[HasTimedPosition]= {
  //
  //  }
}
