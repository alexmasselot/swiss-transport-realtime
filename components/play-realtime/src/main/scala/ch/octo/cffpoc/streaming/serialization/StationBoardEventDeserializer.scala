package ch.octo.cffpoc.streaming.serialization

import java.util

import ch.octo.cffpoc.models._
import ch.octo.cffpoc.stationboard.StationBoardEvent
import ch.octo.cffpoc.stops.Stop
import org.apache.kafka.common.serialization.Deserializer
import org.joda.time.DateTime
import play.api.libs.json._

/**
 * Created by Alexandre Masselot on 02/02/16.
 * © OCTO Technology, 2016
 */
class StationBoardEventDeserializer extends Deserializer[StationBoardEvent] {
  val encoding = "UTF8"

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {

  }

  override def close(): Unit = {

  }

  override def deserialize(topic: String, data: Array[Byte]): StationBoardEvent = {
    if (data == null)
      return null;

    val js: JsValue = Json.parse(new String(data, encoding))
    Json.fromJson[StationBoardEvent](js).get
  }

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

  //  val encoding =
  //    if (props == null)
  //      "UTF8"
  //    else
  //      props.getString("serializer.encoding", "UTF8")

}
