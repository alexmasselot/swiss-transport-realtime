package ch.octo.cffpoc.streaming

import ch.octo.cffpoc.{ TrainPositionSnapshot, GeoLoc, TimedPosition, TrainPosition }
import kafka.serializer.Encoder
import kafka.utils.VerifiableProperties
import play.api.libs.json.Json
import serializers._

/**
 * Created by Alexandre Masselot on 02/02/16.
 * © OCTO Technology
 */
class TrainPositionSnapshotEncoder() extends Encoder[TrainPositionSnapshot] {

  val encoding = "UTF8"

  override def toBytes(s: TrainPositionSnapshot): Array[Byte] =
    if (s == null)
      null
    else
      Json.toJson(s).toString().getBytes(encoding)
}

