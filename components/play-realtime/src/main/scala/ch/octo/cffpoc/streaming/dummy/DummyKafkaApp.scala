package ch.octo.cffpoc.streaming.dummy

import akka.actor.Actor.Receive
import akka.actor.{ Actor, ActorLogging, ActorSystem, Props }
import akka.kafka.ConsumerSettings
import akka.kafka.scaladsl.Consumer
import akka.stream.scaladsl.Sink
import akka.stream.{ ActorMaterializer, Materializer }
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ ByteArrayDeserializer, StringDeserializer }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by alex on 23/05/16.
 */
class ConsumerActor(mat: Materializer, topicName: String) extends Actor with ActorLogging {
  var consumerSettings: ConsumerSettings[Array[Byte], String] = _
  implicit val materializer: Materializer = mat

  override def preStart = {
    consumerSettings = ConsumerSettings(context.system, new ByteArrayDeserializer, new StringDeserializer,
      Set("cffstationboard"))
      .withGroupId("my-group")
      .withBootstrapServers("192.168.99.100:9092")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
  }

  override def receive: Receive = {
    case "GO" => {
      Consumer.plainSource(consumerSettings.withClientId("client1"))
        .runWith(Sink.foreach(println))
    }
  }
}

object DummyKafkaApp extends App {
  implicit val system = ActorSystem("example")
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val ca = system.actorOf(Props(new ConsumerActor(materializer, "cffstationboard")))
  ca ! "GO"

}
