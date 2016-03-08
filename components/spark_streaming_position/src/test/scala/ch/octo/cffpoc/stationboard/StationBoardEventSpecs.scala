package ch.octo.cffpoc.stationboard

import ch.octo.cffpoc.models.GeoLoc
import ch.octo.cffpoc.stops.Stop
import org.scalatest.{ FlatSpec, Matchers }

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

  val decoder = new StationBoardEventDecoder()

  it should "decode, no delay" in {
    val evt = decoder.fromBytes(jsonStr.getBytes)
    evt.timestamp should be(1456767041147L)
    evt.stop should equal(Stop(id = 8500218, name = "Olten", location = GeoLoc(lat = 47.351928, lng = 7.907684)))
  }

}
