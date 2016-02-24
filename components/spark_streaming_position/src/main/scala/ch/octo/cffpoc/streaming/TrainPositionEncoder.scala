package ch.octo.cffpoc.streaming

import ch.octo.cffpoc.{ TimedPosition, GeoLoc, TrainPosition }
import kafka.serializer.Encoder
import kafka.utils.VerifiableProperties
import play.api.libs.json.Json
import serializers._
/**
 * Created by Alexandre Masselot on 02/02/16.
 * © OCTO Technology
 */
class TrainPositionEncoder(props: VerifiableProperties = null) extends Encoder[TrainPosition] {

  val encoding =
    if (props == null)
      "UTF8"
    else
      props.getString("serializer.encoding", "UTF8")

  override def toBytes(s: TrainPosition): Array[Byte] =
    if (s == null)
      null
    else
      Json.toJson(s).toString().getBytes(encoding)
}

