package ch.octo.cffpoc.streaming.serialization

import ch.octo.cffpoc.position.TrainPositionSnapshot
import kafka.serializer.Encoder
import play.api.libs.json.Json

/**
 * Created by Alexandre Masselot on 02/02/16.
 * © OCTO Technology
 */
class TrainPositionSnapshotEncoder() extends Encoder[TrainPositionSnapshot] {
  import serializers._

  val encoding = "UTF8"

  override def toBytes(s: TrainPositionSnapshot): Array[Byte] =
    if (s == null)
      null
    else
      Json.toJson(s).toString().getBytes(encoding)
}

