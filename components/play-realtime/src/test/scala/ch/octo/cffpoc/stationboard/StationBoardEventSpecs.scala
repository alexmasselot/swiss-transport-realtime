package ch.octo.cffpoc.stationboard

import ch.octo.cffpoc.models.{GeoLoc, Train}
import ch.octo.cffpoc.stops.Stop
import ch.octo.cffpoc.streaming.serialization.StationBoardEventDeserializer
import org.joda.time.{DateTime, DateTimeZone}
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration._

/**
 * Created by alex on 17/02/16.
 */
class StationBoardEventSpecs extends FlatSpec with Matchers {
  behavior of "StationBoardEvent"

  val jsonStr =
    """{
      |  "stop": {
      |    "station": {
      |      "id": "8500218",
      |      "name": "Olten",
      |      "score": null,
      |      "coordinate": {
      |        "type": "WGS84",
      |        "x": 47.351928,
      |        "y": 7.907684
      |      },
      |      "distance": null
      |    },
      |    "arrival": null,
      |    "arrivalTimestamp": null,
      |    "departure": "2016-03-01T19:20:00+0100",
      |    "departureTimestamp": 1456856400,
      |    "delay": null,
      |    "platform": "7",
      |    "prognosis": {
      |      "platform": "7",
      |      "arrival": null,
      |      "departure": null,
      |      "capacity1st": 1,
      |      "capacity2nd": 1
      |    },
      |    "realtimeAvailability": null,
      |    "location": {
      |      "id": "8500218",
      |      "name": "Olten",
      |      "score": null,
      |      "coordinate": {
      |        "type": "WGS84",
      |        "x": 47.351928,
      |        "y": 7.907684
      |      },
      |      "distance": null
      |    }
      |  },
      |  "name": "ICN 533",
      |  "category": "ICN",
      |  "categoryCode": 1,
      |  "number": "533",
      |  "operator": "SBB",
      |  "to": "Zürich HB",
      |  "capacity1st": null,
      |  "capacity2nd": null,
      |  "subcategory": "ICN",
      |  "timeStamp": 1456767041147,
      |  "nextStation": {
      |    "station": {
      |      "id": "8500218",
      |      "name": "Olten",
      |      "score": null,
      |      "coordinate": {
      |        "type": "WGS84",
      |        "x": 47.351928,
      |        "y": 7.907684
      |      },
      |      "distance": null
      |    },
      |    "arrival": "2016-03-01T19:20:00+0100",
      |    "arrivalTimestamp": 1456856400,
      |    "departure": null,
      |    "departureTimestamp": null,
      |    "delay": null,
      |    "platform": "",
      |    "prognosis": {
      |      "platform": null,
      |      "arrival": null,
      |      "departure": null,
      |      "capacity1st": null,
      |      "capacity2nd": null
      |    },
      |    "realtimeAvailability": null,
      |    "location": {
      |      "id": "8500218",
      |      "name": "Olten",
      |      "score": null,
      |      "coordinate": {
      |        "type": "WGS84",
      |        "x": 47.351928,
      |        "y": 7.907684
      |      },
      |      "distance": null
      |    }
      |  },
      |  "@version": "1",
      |  "@timestamp": "2016-02-29T17:30:43.461Z"
      |}""".stripMargin

  val jsonStrWithDelay =
    """{
      |  "stop": {
      |    "station": {
      |      "id": "8500218",
      |      "name": "Olten",
      |      "score": null,
      |      "coordinate": {
      |        "type": "WGS84",
      |        "x": 47.351928,
      |        "y": 7.907684
      |      },
      |      "distance": null
      |    },
      |    "arrival": null,
      |    "arrivalTimestamp": null,
      |    "departure": "2016-02-29T18:40:00+0100",
      |    "departureTimestamp": 1456767600,
      |    "delay": 3,
      |    "platform": "7",
      |    "prognosis": {
      |      "platform": "7",
      |      "arrival": null,
      |      "departure": null,
      |      "capacity1st": 1,
      |      "capacity2nd": 1
      |    },
      |    "realtimeAvailability": null,
      |    "location": {
      |      "id": "8500218",
      |      "name": "Olten",
      |      "score": null,
      |      "coordinate": {
      |        "type": "WGS84",
      |        "x": 47.351928,
      |        "y": 7.907684
      |      },
      |      "distance": null
      |    }
      |  },
      |  "name": "ICN 533",
      |  "category": "ICN",
      |  "categoryCode": 1,
      |  "number": "533",
      |  "operator": "SBB",
      |  "to": "Zürich HB",
      |  "capacity1st": null,
      |  "capacity2nd": null,
      |  "subcategory": "ICN",
      |  "timeStamp": 1456767041147,
      |  "nextStation": {
      |    "station": {
      |      "id": "8500218",
      |      "name": "Olten",
      |      "score": null,
      |      "coordinate": {
      |        "type": "WGS84",
      |        "x": 47.351928,
      |        "y": 7.907684
      |      },
      |      "distance": null
      |    },
      |    "arrival": "2016-03-01T19:20:00+0100",
      |    "arrivalTimestamp": 1456856400,
      |    "departure": null,
      |    "departureTimestamp": null,
      |    "delay": null,
      |    "platform": "",
      |    "prognosis": {
      |      "platform": null,
      |      "arrival": null,
      |      "departure": null,
      |      "capacity1st": null,
      |      "capacity2nd": null
      |    },
      |    "realtimeAvailability": null,
      |    "location": {
      |      "id": "8500218",
      |      "name": "Olten",
      |      "score": null,
      |      "coordinate": {
      |        "type": "WGS84",
      |        "x": 47.351928,
      |        "y": 7.907684
      |      },
      |      "distance": null
      |    }
      |  },
      |  "@version": "1",
      |  "@timestamp": "2016-02-29T17:30:43.461Z"
      |}""".stripMargin

  val decoder = new StationBoardEventDeserializer()

  it should "decode, w/o delay" in {
    val evt = decoder.deserialize("pipo", jsonStr.getBytes())
    evt.timestamp.withZone(DateTimeZone.UTC) should be(new DateTime(1456767041147L).withZone(DateTimeZone.UTC))
    evt.stop should equal(Stop(id = 8500218, name = "Olten", location = GeoLoc(lat = 47.351928, lng = 7.907684)))

    evt.delayMinute should be(None)
    evt.arrivalTimestamp should be(None)
    evt.departureTimestamp.map(_.withZone(DateTimeZone.UTC)) should be(Some(new DateTime("2016-03-01T19:20:00+0100").withZone(DateTimeZone.UTC)))
  }

  it should "decode, w/ delay" in {
    val evt = decoder.deserialize("pipo", jsonStrWithDelay.getBytes())
    evt.delayMinute should be(Some(3))
  }

  it should "isWithin, >24h" in {
    val evt = decoder.deserialize("pipo", jsonStr.getBytes())
    evt.isWithin(20 seconds) shouldBe (false)
    evt.isWithin(1 hours) shouldBe (false)
    evt.isWithin(2 hours) shouldBe (false)
    evt.isWithin(2 days) shouldBe (true)
    evt.isWithin(Duration.Inf) shouldBe (true)

    evt.train should equal(Train(
      id = "ICN 533/Zürich HB/2016-03-01T19:20:00+0100",
      name = "ICN 533",
      lastStopName = "Zürich HB",
      category = "ICN"
    ))
  }
  it should "isWithin, 10min" in {
    val evt = decoder.deserialize("pipo", jsonStrWithDelay.getBytes())
    evt.isWithin(20 seconds) shouldBe (false)
    evt.isWithin(5 minutes) shouldBe (false)
    evt.isWithin(15 minutes) shouldBe (true)
    evt.isWithin(1 hours) shouldBe (true)
    evt.isWithin(Duration.Inf) shouldBe (true)
  }
}
