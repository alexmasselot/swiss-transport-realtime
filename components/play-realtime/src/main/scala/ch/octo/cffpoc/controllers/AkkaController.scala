package ch.octo.cffpoc.controllers

import akka.actor.{ ActorRef, Props, ActorSystem }
import akka.stream.ActorMaterializer
import ch.octo.cffpoc.streaming.app.akka.actors.{ StationBoardActor, MainActor }
import play.api.{ Configuration, Environment }
import play.api.mvc.Controller

/**
 * Created by alex on 06/05/16.
 */
//trait AkkaController extends Controller {
//  def environment: Environment
//  def configuration: Configuration
//  def actorSystem: ActorSystem
//
//  def mainActor = {
//    actorSystem.ch
//      .child(stationBoardActorName(stop))
//      .getOrElse(context.actorOf(Props(classOf[StationBoardActor], stop, statsActor.path), stationBoardActorName(stop)))
//  }
//
//  lazy val mainActor = AkkaController.mainActor(configuration)
//}
//
//object AkkaController {
//  var _mainActor: ActorRef = null
//
//  implicit val actorSystem = ActorSystem("cff-streaming")
//  implicit val materializer = ActorMaterializer()
//
//  def mainActor(configuration: Configuration): ActorRef = {
//    if (_mainActor == null) {
//      _mainActor = actorSystem.actorOf(Props(classOf[MainActor], "192.168.99.100:9092", "192.168.99.100:2181"), "main")
//    }
//    _mainActor
//  }
//}