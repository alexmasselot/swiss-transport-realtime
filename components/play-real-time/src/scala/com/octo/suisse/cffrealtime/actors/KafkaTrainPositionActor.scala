package com.octo.suisse.cffrealtime.actors

import akka.actor.{ ActorLogging, Props, ActorRef, Actor }
import akka.actor.Actor.Receive
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{ Sink, Source }
import com.octo.suisse.cffrealtime.streaming.TrainPositionDecoder
import kafka.serializer.{ StringEncoder, StringDecoder }
import com.softwaremill.react.kafka.{ ReactiveKafka, ProducerProperties, ConsumerProperties }
import org.apache.kafka.common.serialization.StringDeserializer
import org.reactivestreams.Subscriber

/**
 * Created by alex on 31/12/15.
 */
class KafkaTrainPositionActor extends Actor with ActorLogging {
  implicit val actorSystem = context.system

  log.info("new KafkaTrainPositionActor")

  override def receive: Receive = {
    case "INIT" =>
      log.info("INIT KafkaTrainPositionActor")
      implicit val materializer = ActorMaterializer()
      val kafka = new ReactiveKafka()

      val publisherProperties = ConsumerProperties(
        brokerList = "192.168.99.108:9092",
        zooKeeperHost = "192.168.99.108:2181",
        topic = "test",
        decoder = new StringDecoder() //new TrainPositionDecoder()
      )
      val subscriber: Subscriber[String] = kafka.publish(ProducerProperties(
        brokerList = "192.168.99.108::9092",
        topic = "test_2",
        encoder = new StringEncoder()
      ))
      val publisherActorProps: Props = kafka.consumerActorProps(publisherProperties)
      val publisherActor: ActorRef = context.actorOf(publisherActorProps)
      val ff = Source(kafka.consume(publisherProperties)).map({ case x: Any => log.info(s"HAHA $x"); "!!!" + x.message() })
        //.to(Sink.fromSubscriber(subscriber))
        //.to(Sink.foreach({ case x => log.info(s" GOT >>> $x") }))
        .to(Sink.foreach({ (x: String) => log.info(s" GOT >>> $x") }))

      ff.named("pipo").run()
      log.info(s"ff=$ff")
      Thread.sleep(5000)

  }
}
