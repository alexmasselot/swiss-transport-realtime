package com.octo.suisse.cffrealtime.streaming

import com.octo.suisse.cffrealtime.models._
import org.specs2.mutable.Specification
import play.api.libs.json.Json

/**
 * Created by alex on 30/12/15.
 */
class TrainPositionDecoderSpecs extends Specification {
  import TrainPositionDecoder._

  val jsSample = Json.parse("""{"x":"6602810","y":"46547070","name":"R 144   ","trainrefdate":"30.12.15","category":"R","trainid":"84/56888/18/28/95","direction":"26","prodclass":"32","passproc":"","lstopname":"Lausanne-Flon","poly":[{"x":"6602810","y":"46547070","passproc":"","msec":"0","direction":"26"},{"x":"6602945","y":"46546917","passproc":"","msec":"2000","direction":"26"},{"x":"6603080","y":"46546773","passproc":"","msec":"4000","direction":"26"},{"x":"6603215","y":"46546621","passproc":"","msec":"6000","direction":"26"},{"x":"6603349","y":"46546468","passproc":"","msec":"8000","direction":"26"},{"x":"6603475","y":"46546315","passproc":"","msec":"10000","direction":"26"},{"x":"6603610","y":"46546162","passproc":"","msec":"12000","direction":"26"},{"x":"6603745","y":"46546018","passproc":"","msec":"14000","direction":"26"},{"x":"6603880","y":"46545865","passproc":"","msec":"16000","direction":"26"},{"x":"6604006","y":"46545722","passproc":"","msec":"18000","direction":"26"},{"x":"6604141","y":"46545569","passproc":"","msec":"20000","direction":"26"},{"x":"6604275","y":"46545416","passproc":"","msec":"22000","direction":"26"},{"x":"6604410","y":"46545263","passproc":"","msec":"24000","direction":"26"},{"x":"6604545","y":"46545110","passproc":"","msec":"26000","direction":"26"},{"x":"6604671","y":"46544967","passproc":"","msec":"28000","direction":"26"},{"x":"6604806","y":"46544814","passproc":"","msec":"30000","direction":"26"}],"timeStamp":1451486897785}""")

  "TrainPositionDecoder" should {
    "json read GeoCoordinates" in {
      val gc = Json.fromJson[GeoCoordinates](jsSample).get
      gc must beEqualTo(GeoCoordinates(46.547070, 6.602810))
    }
    "json read Train" in {
      val train = Json.fromJson[Train](jsSample).get
      train must beEqualTo(Train("84/56888/18/28/95", "R 144", "R", "Lausanne-Flon"))
    }
    "json read TrainPosition" in {
      val tp = Json.fromJson[TrainPosition](jsSample).get
      tp must beEqualTo(TrainPosition(Train("84/56888/18/28/95", "R 144", "R", "Lausanne-Flon"), GeoCoordinates(46.547070, 6.602810)))
    }
  }
}
