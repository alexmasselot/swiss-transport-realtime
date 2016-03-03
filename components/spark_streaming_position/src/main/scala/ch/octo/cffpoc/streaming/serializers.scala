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

  implicit val formatTimedPositionWithStop = Json.format[TimedPositionWithStop]
  implicit val formatTimedPosition = Json.format[TimedPosition]

  implicit object formatHasTimedPosition extends Format[HasTimedPosition] {
    override def writes(o: HasTimedPosition): JsValue =
      o match {
        case t: TimedPosition => Json.writes[TimedPosition].writes(t)
        case t: TimedPositionIsMoving => Json.writes[TimedPositionIsMoving].writes(t)
        case t: TimedPositionWithStop => Json.writes[TimedPositionWithStop].writes(t)
      }

    override def reads(json: JsValue): JsResult[HasTimedPosition] = json match {
      case t: TimedPosition => Json.reads[TimedPosition].reads(t)
      case t: TimedPositionIsMoving => Json.reads[TimedPositionIsMoving].reads(t)
      case t: TimedPositionWithStop => Json.reads[TimedPositionWithStop].reads(t)
    }
  }

  implicit val formatTrainPosition = Json.format[TrainPosition]
  implicit val formatTrainPositionSnapshot = Json.format[TrainPositionSnapshot]

  //  val hasTimedPositionReads: Reads[HasTimedPosition]= {
  //
  //  }
}
