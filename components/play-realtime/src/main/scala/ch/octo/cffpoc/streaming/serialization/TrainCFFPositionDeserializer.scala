package ch.octo.cffpoc.streaming.serialization

import java.util

import ch.octo.cffpoc.models._
import ch.octo.cffpoc.position.{ TimedPosition, TrainCFFPosition, TrainPosition }
import org.apache.kafka.common.serialization.Deserializer
import org.joda.time.{ DateTime, DateTimeZone }
import play.api.libs.json._

/**
 * Created by Alexandre Masselot on 02/02/16.
 * © OCTO Technology, 2016
 */
class TrainCFFPositionDeserializer extends Deserializer[TrainCFFPosition] {
  val encoding = "UTF8"
  val dateTimeZone = DateTimeZone.forOffsetHours(1)

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {

  }

  override def close(): Unit = {

  }

  override def deserialize(topic: String, data: Array[Byte]): TrainCFFPosition = {
    val js: JsValue = Json.parse(new String(data, encoding))
    Json.fromJson[TrainCFFPosition](js).get
  }

  implicit val readsTimeStamp = new Reads[DateTime] {
    override def reads(json: JsValue): JsResult[DateTime] = {
      JsSuccess(new DateTime(json.as[Long], dateTimeZone))
    }
  }

  /*
    the cff CGI query exports a poly list of the futur planned positions
   */
  case class PolyPos(location: GeoLocBearing, msec: Int)

  implicit val readsPolyPos = new Reads[PolyPos] {
    override def reads(json: JsValue): JsResult[PolyPos] = {
      val dir = (json \ "direction").as[String] match {
        case "" => None
        case s: String => Some(s.toDouble * 10)
      }
      JsSuccess(PolyPos(
        location = GeoLocBearing((json \ "y").as[String].toDouble / 1000000, (json \ "x").as[String].toDouble / 1000000, dir),
        msec = (json \ "msec").as[String].toInt
      ))
    }
  }

  implicit val readsTrainCFFPosition = new Reads[TrainCFFPosition] {

    override def reads(json: JsValue): JsResult[TrainCFFPosition] = {
      val tStamp = (json \ "timeStamp").as[DateTime]
      JsSuccess(TrainCFFPosition(
        current = TrainPosition(
          train = Train(
            id = (json \ "trainid").as[String],
            name = (json \ "name").as[String] trim,
            category = (json \ "category").as[String] trim,
            lastStopName = (json \ "lstopname").as[String] trim
          ),
          timedPosition = TimedPosition(
            timestamp = tStamp,
            position = GeoLocBearing((json \ "y").as[String].toDouble / 1000000,
              (json \ "x").as[String].toDouble / 1000000,
              (json \ "direction").as[String] match {
                case "" => None
                case s: String => Some(s.toDouble * 10)
              }
            )
          )
        ),
        futurePositions = (json \ "poly").as[List[PolyPos]].map(pp => TimedPosition(tStamp.plusMillis(pp.msec), pp.location))
      ))
    }
  }

}

