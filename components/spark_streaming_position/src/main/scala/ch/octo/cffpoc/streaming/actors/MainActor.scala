package ch.octo.cffpoc.streaming.app.akka.actors

import akka.actor._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{ Sink, Source }
import ch.octo.cffpoc.stationboard.{ StationBoard, StationBoardsSnapshotStats }
import ch.octo.cffpoc.streaming.app.akka.actors.Messages.{ GetGlobalStats, StationBoardDetails }
import ch.octo.cffpoc.streaming.serialization.StationBoardEventDecoder
import com.softwaremill.react.kafka.{ ConsumerProperties, ProducerProperties, ReactiveKafka }
import kafka.serializer.StringEncoder
import org.reactivestreams.Subscriber

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
 * Created by alex on 30/03/16.
 */
class MainActor extends Actor with ActorLogging {

  implicit val actorSystem = ActorSystem("ReactiveKafka")

  implicit val materializer = ActorMaterializer()

  val kafka = new ReactiveKafka()
  val publisher = kafka.consume(ConsumerProperties(
    brokerList = "192.168.99.100:9092",
    zooKeeperHost = "192.168.99.100:2181",
    topic = "cff_station_board",
    groupId = "groupName",
    decoder = new StationBoardEventDecoder()
  )) //.readFromEndOfStream())

  val stationBoardMasterActor = context.actorOf(Props[StationBoardMasterActor], "station-board")

  Source.fromPublisher(publisher) //.map(_.message().toUpperCase)
    //.to(Sink.fromSubscriber(subscriber)).run()
    .to(Sink.foreach({
      m =>
        stationBoardMasterActor ! m.message()
    })).run()

  val subscriber: Subscriber[String] = kafka.publish(ProducerProperties(
    brokerList = "192.168.99.100:9092",
    topic = "dummy",
    encoder = new StringEncoder()
  ))

  val cancellable2 =
    context.system.scheduler.schedule(100 milliseconds,
      5000 milliseconds,
      stationBoardMasterActor,
      StationBoardDetails(8501120L))

  val subscriberProperties = ProducerProperties(
    brokerList = "192.168.99.100:9092",
    topic = "dummy",
    encoder = new StringEncoder()
  )
  val topLevelSubscriberActor: ActorRef = kafka.producerActor(subscriberProperties)

  override def receive: Receive = {
    case m: StationBoardDetails => stationBoardMasterActor forward (m)
    case GetGlobalStats => stationBoardMasterActor forward (GetGlobalStats)
    case statsSnapshot: StationBoardsSnapshotStats => ???
    case board: StationBoard => ???
    case x => log.warning(s"lost message $x")
  }
}

object MainActor {
  implicit val actorSystem = ActorSystem("cff-streaming")
  implicit val materializer = ActorMaterializer()

  lazy val mainActor = actorSystem.actorOf(Props[MainActor])

  def apply() = mainActor
}