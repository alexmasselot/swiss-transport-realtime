package ch.octo.cffpoc.streaming.app.akka

import ch.octo.cffpoc.streaming.app.akka.actors.MainActor
import ch.octo.cffpoc.streaming.app.akka.actors.Messages.GetGlobalStats

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object AkkaStreamApp extends App {

  //  val subscriber: Subscriber[String] = kafka.publish(ProducerProperties(
  //    brokerList = "192.168.99.100:9092",
  //    topic = "dummy",
  //    encoder = new StringEncoder()
  //  ))

  MainActor()

  val cancellable =
    MainActor.actorSystem.scheduler.schedule(100 milliseconds,
      1000 milliseconds,
      MainActor(),
      GetGlobalStats)
}

