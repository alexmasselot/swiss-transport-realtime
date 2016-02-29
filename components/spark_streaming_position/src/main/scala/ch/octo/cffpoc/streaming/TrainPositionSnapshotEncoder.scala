package ch.octo.cffpoc.streaming

import ch.octo.cffpoc.models.TrainPositionSnapshot
import ch.octo.cffpoc.streaming.serializers._
import kafka.serializer.Encoder
import play.api.libs.json.Json

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

