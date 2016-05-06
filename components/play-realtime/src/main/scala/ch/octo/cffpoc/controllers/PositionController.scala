package ch.octo.cffpoc.controllers

import javax.inject.{ Singleton, Inject }

import akka.actor.{ Props, ActorSystem }
import akka.pattern.ask
import akka.util.Timeout
import ch.octo.cffpoc.position.{ TrainPosition, TrainPositionSnapshot }
import ch.octo.cffpoc.streaming.app.akka.actors.{ MainActorGenerator, MainActor }
import ch.octo.cffpoc.streaming.app.akka.actors.Messages._
import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
 * Created by alex on 30/03/16.
 */
class PositionController @Inject() (configuration: Configuration, actorSystem: ActorSystem, mainActorGenerator: MainActorGenerator) extends Controller {
  //@Inject() (val environment: play.api.Environment, val configuration: play.api.Configuration) extends AkkaController {

  import ch.octo.cffpoc.streaming.serialization.serializers._

  implicit val timeout = Timeout(5 seconds)
  implicit val config = configuration

  val mainActor = mainActorGenerator.mainActor

  def snapshot = Action.async {

    (mainActor ? PositionSnapshot).mapTo[TrainPositionSnapshot].map { snapshot =>
      //Ok(Json.toJson(message))
      Ok("train_id\ttrain_category\ttrain_name\ttrain_lastStopName\tposition_lat\tposition_lng\tposition_bearing\n" +
        snapshot.positions.values.map({
          p =>
            s"""${p.train.id}\t${p.train.category}\t${p.train.name.trim()}\t${p.train.lastStopName}\t${p.timedPosition.position.lat}\t${p.timedPosition.position.lng}\t${p.timedPosition.position.bearing.map(_.toString).getOrElse("")}"""
        }).mkString("\n")
      )
    }
  }

  def details(id: String) = Action.async {
    (mainActor ? PositionDetails(id.trim)).mapTo[Option[TrainPosition]].map {
      _ match {
        case Some(tp) => Ok(Json.toJson(tp))
        case None => NotFound

      }
    }
  }

}
