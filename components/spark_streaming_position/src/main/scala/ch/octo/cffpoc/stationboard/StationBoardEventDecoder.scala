package ch.octo.cffpoc.stationboard

import ch.octo.cffpoc.models.{ GeoLoc, TimedPosition, TrainCFFPosition, TrainPosition }
import ch.octo.cffpoc.stops.Stop
import kafka.serializer.Decoder
import kafka.utils.VerifiableProperties
import play.api.libs.json._

/**
 * Created by Alexandre Masselot on 02/02/16.
 * © OCTO Technology, 2016
 */
class StationBoardEventDecoder(props: VerifiableProperties = null) extends Decoder[StationBoardEvent] {

  implicit val readsStationBoardEvent = new Reads[StationBoardEvent] {
    override def reads(json: JsValue): JsResult[StationBoardEvent] = {
      val tStamp = (json \ "timeStamp").as[Long]
      JsSuccess(StationBoardEvent(
        timestamp = tStamp,
        stop = Stop(
          id = (json \ "stop" \ "station" \ "id").as[String].toLong,
          name = (json \ "stop" \ "station" \ "name").as[String],
          location = GeoLoc(
            lat = (json \ "stop" \ "station" \ "coordinate" \ "x").as[Double],
            lng = (json \ "stop" \ "station" \ "coordinate" \ "y").as[Double]
          )
        ),
        arrivalTimestamp = None,
        departureTimestamp = None,
        delayMinute = None
      ))
    }
  }

  val encoding =
    if (props == null)
      "UTF8"
    else
      props.getString("serializer.encoding", "UTF8")

  def fromBytes(bytes: Array[Byte]): StationBoardEvent = {
    val js: JsValue = Json.parse(new String(bytes, encoding))
    Json.fromJson[StationBoardEvent](js).get
  }
}
