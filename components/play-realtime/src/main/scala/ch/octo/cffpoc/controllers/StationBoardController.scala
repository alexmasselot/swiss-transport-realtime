package ch.octo.cffpoc.controllers

import akka.pattern.ask
import akka.util.Timeout
import ch.octo.cffpoc.stationboard.{ StationBoard, StationBoardsSnapshotStats }
import ch.octo.cffpoc.streaming.app.akka.actors.MainActor
import ch.octo.cffpoc.streaming.app.akka.actors.Messages.{ StationBoardDetails, StationBoardsSnapshot }
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
 * Created by alex on 30/03/16.
 */
class StationBoardController extends Controller {
  import ch.octo.cffpoc.streaming.serialization.serializers._
  implicit val timeout = Timeout(5 seconds)

  def snapshot = Action.async {
    (MainActor() ? StationBoardsSnapshot).mapTo[StationBoardsSnapshotStats].map { message =>
      Ok(Json.toJson(message))
    }
  }

  def details(id: Long) = Action.async {
    (MainActor() ? StationBoardDetails(id)).mapTo[StationBoard].map { message =>
      Ok(Json.toJson(message))
    }
  }
}
