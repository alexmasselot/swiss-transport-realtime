package ch.octo.cffpoc.controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import ch.octo.cffpoc.stationboard.{ StationBoard, StationBoardsSnapshotStats }
import ch.octo.cffpoc.streaming.app.akka.actors.MainActorGenerator
import ch.octo.cffpoc.streaming.app.akka.actors.Messages.{ StationBoardDetails, StationBoardsSnapshot }
import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
 * Created by alex on 30/03/16.
 */
class StationBoardController @Inject() (configuration: Configuration, actorSystem: ActorSystem, mainActorGenerator: MainActorGenerator) extends Controller {
  //@Inject() (val environment: play.api.Environment, val configuration: play.api.Configuration) extends AkkaController {
  import ch.octo.cffpoc.streaming.serialization.serializers._

  implicit val timeout = Timeout(5 seconds)
  implicit val config = configuration

  val mainActor = mainActorGenerator.mainActor

  def snapshot = Action.async {
    (mainActor ? StationBoardsSnapshot).mapTo[StationBoardsSnapshotStats].map { message =>
      Ok(Json.toJson(message))
    }
  }

  def details(id: Long) = Action.async {
    (mainActor ? StationBoardDetails(id)).mapTo[StationBoard].map { message =>
      Ok(Json.toJson(message))
    }
  }
}
