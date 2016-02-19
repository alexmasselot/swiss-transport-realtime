package ch.octo.cffpoc.streaming

import ch.octo.cffpoc.{ TrainPosition, TimedPosition, GeoLoc, TrainCFFPosition }
import kafka.serializer.Decoder
import kafka.utils.VerifiableProperties
import play.api.libs.json._

/**
 * Created by alex on 02/02/16.
 */
class TrainCFFPositionDecoder(props: VerifiableProperties = null) extends Decoder[TrainCFFPosition] {

  /*
    the cff CGI query exports a poly list of the futur planned positions
   */
  case class PolyPos(location: GeoLoc, msec: Int)

  implicit val readsPolyPos = new Reads[PolyPos] {
    override def reads(json: JsValue): JsResult[PolyPos] = {
      JsSuccess(PolyPos(
        location = GeoLoc(((json \ "y").as[String].toDouble) / 1000000, ((json \ "x").as[String].toDouble) / 1000000),
        msec = (json \ "msec").as[String].toInt
      ))
    }
  }

  implicit val readsTrainCFFPosition = new Reads[TrainCFFPosition] {
    override def reads(json: JsValue): JsResult[TrainCFFPosition] = {
      val tStamp = (json \ "timeStamp").as[Long]
      JsSuccess(TrainCFFPosition(
        current = TrainPosition(
          trainid = (json \ "trainid").as[String],
          name = (json \ "name").as[String] trim,
          category = (json \ "category").as[String] trim,
          lastStopName = (json \ "lstopname").as[String] trim,
          timeStamp = tStamp,
          location = GeoLoc(((json \ "y").as[String].toDouble) / 1000000, ((json \ "x").as[String].toDouble) / 1000000)
        ),
        futurePositions = (json \ "poly").as[List[PolyPos]].map(pp => TimedPosition(tStamp + pp.msec, pp.location))
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
