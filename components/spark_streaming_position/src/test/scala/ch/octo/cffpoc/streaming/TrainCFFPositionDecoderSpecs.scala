package ch.octo.cffpoc.streaming

import ch.octo.cffpoc.models.{ GeoLocBearing, GeoLoc }
import ch.octo.cffpoc.position.TimedPosition
import ch.octo.cffpoc.streaming.serialization.TrainCFFPositionDecoder
import org.joda.time.DateTime
import org.scalatest.{ FlatSpec, Matchers }

/**
 * Created by Alexandre Masselot on 17/02/16.
 * © OCTO Technology
 */
class TrainCFFPositionDecoderSpecs extends FlatSpec with Matchers {
  behavior of "TrainCFFPositionDecoder"

  it should "decode" in {
    val bs: Array[Byte] =
      """{"x":"7589565",
        "y":"47547399",
        "name":"ICE 71  ",
        "trainrefdate": "12.02.16",
        "category":"ICE",
        "trainid":"84/27911/18/19/95",
        "direction":"30",
        "prodclass":"1",
        "passproc":"",
        "lstopname":"Chur",
        "poly":[
        {"x":"7589565","y":"47547399","passproc":"","msec":"0","direction":"5"},
        {"x":"7589565","y":"47547399","passproc":"50","msec":"2000","direction":"5"},
        {"x":"7589565","y":"47547399","passproc":"","msec":"4000","direction":"5"},
        {"x":"7589565","y":"47547399","passproc":"","msec":"6000","direction":""},
        {"x":"7589565","y":"47547399","passproc":"","msec":"8000","direction":"5"},{"x":"7589565","y":"47547399","passproc":"","msec":"10000","direction":"5"},{"x":"7589565","y":"47547399","passproc":"","msec":"12000","direction":"5"},{"x":"7589565","y":"47547399","passproc":"","msec":"14000","direction":"5"},{"x":"7589565","y":"47547399","passproc":"","msec":"16000","direction":"5"},{"x":"7589565","y":"47547399","passproc":"","msec":"18000","direction":"5"},{"x":"7589565","y":"47547399","passproc":"","msec":"20000","direction":"5"},{"x":"7589565","y":"47547399","passproc":"","msec":"22000","direction":"5"},{"x":"7589565","y":"47547399","passproc":"","msec":"24000","direction":"5"},{"x":"7589565","y":"47547399","passproc":"","msec":"26000","direction":"5"},{"x":"7589565","y":"47547399","passproc":"","msec":"28000","direction":"5"},{"x":"7589565","y":"47547399","passproc":"","msec":"30000","direction":"5"}],"timeStamp":1455742294099,"@version":"1","@timestamp":"2016-02-12T12:05:45.302Z"}""".getBytes
    val tp = new TrainCFFPositionDecoder().fromBytes(bs)
    val current = tp.current
    current.train.id should equal("84/27911/18/19/95")
    current.train.name should equal("ICE 71")
    current.train.lastStopName should equal("Chur")
    current.train.category should equal("ICE")
    current.timedPosition.position should equal(GeoLocBearing(47.547399, 7.589565, Some(300)))
    current.timedPosition.timestamp should equal(new DateTime(1455742294099L))

    tp.futurePositions should have length 16
    tp.futurePositions(2) should equal(TimedPosition(new DateTime(1455742294099L).plusMillis(4000), GeoLocBearing(47.547399, 7.589565, Some(50))))
    //None position at 3
    tp.futurePositions(3) should equal(TimedPosition(new DateTime(1455742296099L).plusMillis(4000), GeoLocBearing(47.547399, 7.589565, None)))
  }
}
