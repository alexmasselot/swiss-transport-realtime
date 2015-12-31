package com.octo.suisse.cffrealtime.streaming

import com.octo.suisse.cffrealtime.models.{ Train, GeoCoordinates, TrainPosition }
import kafka.serializer.Decoder
import kafka.utils.VerifiableProperties

import play.api.libs.json._ // JSON library
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.functional.syntax._ // Combinator syntax

/**
 * Created by alex on 30/12/15.
 */
class TrainPositionDecoder(props: VerifiableProperties = null) extends Decoder[TrainPosition] {
  import TrainPositionDecoder._

  val encoding =
    if (props == null)
      "UTF8"
    else
      props.getString("serializer.encoding", "UTF8")

  def fromBytes(bytes: Array[Byte]): TrainPosition = {
    val jsVal = Json.parse(new String(bytes, encoding))

    Json.fromJson[TrainPosition](jsVal).get
  }

}

object TrainPositionDecoder {
  def trGeo(x: String, y: String) = GeoCoordinates(x.toDouble / 1000000, y.toDouble / 1000000)
  def trTrain(id: String, name: String, category: String, lastStopName: String) = Train(id.trim, name.trim, category.trim, lastStopName.trim)

  implicit val geoCoordinateRead: Reads[GeoCoordinates] =
    (
      (JsPath \ "y").read[String] and
      (JsPath \ "x").read[String]
    )(trGeo _)

  implicit val TrainRead: Reads[Train] =
    (
      (JsPath \ "trainid").read[String] and
      (JsPath \ "name").read[String] and
      (JsPath \ "category").read[String] and
      (JsPath \ "lstopname").read[String]
    )(trTrain _)

  implicit val TrainPositionsRead: Reads[TrainPosition] =
    (
      (JsPath).read[Train] and
      (JsPath).read[GeoCoordinates]
    )(TrainPosition.apply _)
}