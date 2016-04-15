package ch.octo.cffpoc.streaming.serialization

import ch.octo.cffpoc.models._
import ch.octo.cffpoc.stationboard.StationBoardEvent
import ch.octo.cffpoc.stops.Stop
import kafka.serializer.Decoder
import kafka.utils.VerifiableProperties
import org.joda.time.DateTime
import play.api.libs.json._

/**
 * Created by Alexandre Masselot on 02/02/16.
 * © OCTO Technology, 2016
 */
class StationBoardEventDecoder(props: VerifiableProperties = null) extends Decoder[StationBoardEvent] {

  implicit val readsStationBoardEvent = new Reads[StationBoardEvent] {
    override def reads(json: JsValue): JsResult[StationBoardEvent] = {
      JsSuccess(StationBoardEvent(
        timestamp = new DateTime((json \ "timeStamp").as[Long]),
        stop = Stop(
          id = (json \ "stop" \ "station" \ "id").as[String].toLong,
          name = (json \ "stop" \ "station" \ "name").as[String],
          location = GeoLoc(
            lat = (json \ "stop" \ "station" \ "coordinate" \ "x").as[Double],
            lng = (json \ "stop" \ "station" \ "coordinate" \ "y").as[Double]
          )
        ),
        train = Train(
          id = (json \ "name").as[String] + "/" + (json \ "to").as[String] + "/" + (json \ "stop" \ "departure").as[String],
          name = (json \ "name").as[String],
          lastStopName = (json \ "to").as[String],
          category = (json \ "category").as[String]
        ),
        arrivalTimestamp = (json \ "stop" \ "arrivalTimestamp").asOpt[Long].map(l => new DateTime(l * 1000)),
        departureTimestamp = (json \ "stop" \ "departureTimestamp").asOpt[Long].map(l => new DateTime(l * 1000)),
        delayMinute = (json \ "stop" \ "delay").asOpt[Int],
        platform = (json \ "stop" \ "platform").asOpt[String]
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
