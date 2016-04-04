package ch.octo.cffpoc.streaming.serialization

import ch.octo.cffpoc.models._
import ch.octo.cffpoc.position._
import ch.octo.cffpoc.stationboard._
import ch.octo.cffpoc.stops.Stop
import play.api.libs.json._

/**
 * Created by alex on 24/02/16.
 */
object serializers {

  implicit object writesGeoLoc extends Writes[GeoLoc] {
    override def writes(obj: GeoLoc): JsValue = obj match {
      case o: GeoLocBearing => JsObject {
        List(
          "lat" -> JsNumber(o.lat),
          "lng" -> JsNumber(o.lng),
          "bearing" -> JsNumber(o.bearing)
        )
      }
      case o: GeoLoc => Json.writes[GeoLoc].writes(o)
    }
  }

  implicit val writesStop = Json.writes[Stop]

  implicit object formatHasTimedPosition extends Writes[HasTimedPosition] {
    override def writes(o: HasTimedPosition): JsValue =
      o match {
        case t: TimedPosition => Json.writes[TimedPosition].writes(t)
        case t: TimedPositionIsMoving => Json.writes[TimedPositionIsMoving].writes(t)
        case t: TimedPositionWithStop => Json.writes[TimedPositionWithStop].writes(t)
      }
  }

  implicit val writesTrain = Json.writes[Train]
  implicit val writesTrainPosition = Json.writes[TrainPosition]

  implicit object writesTrainPositionSnapshot extends Writes[TrainPositionSnapshot] {
    override def writes(o: TrainPositionSnapshot): JsValue = JsObject(
      List(
        ("timestamp" -> JsNumber(o.timestamp.getMillis)),
        ("positions" -> o.positions.foldLeft(new JsObject(Map()))((acc, e) => acc + (e._1, Json.toJson(e._2)))
        )
      )
    )
  }

  implicit val writesSationEvent = Json.writes[StationBoardEvent]
  implicit val writesSationBoard = Json.writes[StationBoard]
  implicit val writerStationBoardStats = Json.writes[StationBoardStats]

  implicit object writesSationBoardSnapshot extends Writes[StationBoardsSnapshot] {
    override def writes(o: StationBoardsSnapshot): JsValue = JsObject(
      List(
        ("timestamp" -> JsNumber(o.timestamp.getMillis)),
        ("boards" -> Json.toJson(o.boards.values.toList.sortBy(_.stop.name)))
      )
    )
  }

  implicit object writesSationBoardSnapshotStats extends Writes[StationBoardsSnapshotStats] {
    override def writes(o: StationBoardsSnapshotStats): JsValue = JsObject(
      List(
        ("timestamp" -> JsNumber(o.timestamp.getMillis)),
        ("stats" -> Json.toJson(o.stats.values.toList.sortBy(_.stop.name)))
      )
    )
  }

  //  val hasTimedPositionReads: Reads[HasTimedPosition]= {
  //
  //  }
}

