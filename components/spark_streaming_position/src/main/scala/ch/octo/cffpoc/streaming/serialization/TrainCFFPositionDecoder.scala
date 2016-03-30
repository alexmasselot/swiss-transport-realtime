package ch.octo.cffpoc.streaming.serialization

import ch.octo.cffpoc.models._
import ch.octo.cffpoc.position.{ TimedPosition, TrainCFFPosition, TrainPosition }
import kafka.serializer.Decoder
import kafka.utils.VerifiableProperties
import org.joda.time.DateTime
import play.api.libs.json._

/**
 * Created by Alexandre Masselot on 02/02/16.
 * © OCTO Technology, 2016
 */
class TrainCFFPositionDecoder(props: VerifiableProperties = null) extends Decoder[TrainCFFPosition] {
  implicit val readsTimeStamp = new Reads[DateTime] {
    override def reads(json: JsValue): JsResult[DateTime] = {
      JsSuccess(new DateTime(json.as[Long]))
    }
  }

  /*
    the cff CGI query exports a poly list of the futur planned positions
   */
  case class PolyPos(location: GeoLoc, msec: Int)

  implicit val readsPolyPos = new Reads[PolyPos] {
    override def reads(json: JsValue): JsResult[PolyPos] = {
      JsSuccess(PolyPos(
        location = GeoLoc((json \ "y").as[String].toDouble / 1000000, (json \ "x").as[String].toDouble / 1000000),
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
            position = GeoLoc((json \ "y").as[String].toDouble / 1000000, (json \ "x").as[String].toDouble / 1000000)
          )
        ),
        futurePositions = (json \ "poly").as[List[PolyPos]].map(pp => TimedPosition(tStamp.plusMillis(pp.msec), pp.location))
      ))
    }
  }

  val encoding =
    if (props == null)
      "UTF8"
    else
      props.getString("serializer.encoding", "UTF8")

  def fromBytes(bytes: Array[Byte]): TrainCFFPosition = {
    val js: JsValue = Json.parse(new String(bytes, encoding))
    Json.fromJson[TrainCFFPosition](js).get
  }
}

