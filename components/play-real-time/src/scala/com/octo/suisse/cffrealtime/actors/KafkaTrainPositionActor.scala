package com.octo.suisse.cffrealtime.actors

import akka.actor.{ ActorLogging, Props, ActorRef, Actor }
import akka.actor.Actor.Receive
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{ Sink, Source }
import com.octo.suisse.cffrealtime.streaming.TrainPositionDecoder
import kafka.serializer.StringDecoder
import com.softwaremill.react.kafka.{ ReactiveKafka, ProducerProperties, ConsumerProperties }

/**
 * Created by alex on 31/12/15.
 */
class KafkaTrainPositionActor extends Actor with ActorLogging {
  val kafka = new ReactiveKafka()
  implicit val actorSystem = context.system
  implicit val materializer = ActorMaterializer()

  log.info("new KafkaTrainPositionActor")
  override def receive: Receive = {
    case "INIT" =>
      log.info("INIT KafkaTrainPositionActor")
      val publisherProperties = ConsumerProperties(
        brokerList = "192.168.99.103:9092",
        zooKeeperHost = "192.168.99.103:2181",
        topic = "cff_train_position",
        groupId = "akka-stream",
        decoder = new StringDecoder() //new TrainPositionDecoder()
      )
      val publisherActorProps: Props = kafka.consumerActorProps(publisherProperties)
      val publisherActor: ActorRef = context.actorOf(publisherActorProps)
      Source.fromPublisher(kafka.consume(publisherProperties)).to(Sink.foreach({ x: Any => println(s" GOT >>> $x") })).run()
  }
}
