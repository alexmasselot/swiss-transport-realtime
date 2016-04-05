package ch.octo.cffpoc.streaming.app.akka.actors

import akka.actor._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{ Sink, Source }
import ch.octo.cffpoc.stationboard.{ StationBoard, StationBoardsSnapshotStats }
import ch.octo.cffpoc.streaming.actors.PositionMasterActor
import ch.octo.cffpoc.streaming.app.akka.actors.Messages.{ PositionSnapshot, StationBoardsSnapshot, StationBoardDetails }
import ch.octo.cffpoc.streaming.serialization.{ TrainCFFPositionDecoder, StationBoardEventDecoder }
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

  val stationBoardMasterActor = context.actorOf(Props[StationBoardMasterActor], "station-board")
  val positionMasterActor = context.actorOf(Props[PositionMasterActor], "position")

  val kafka = new ReactiveKafka()

  val publisherStationBoards = kafka.consume(ConsumerProperties(
    brokerList = "192.168.99.100:9092",
    zooKeeperHost = "192.168.99.100:2181",
    topic = "cff_station_board",
    groupId = "cff_streaming_akka",
    decoder = new StationBoardEventDecoder()
  ).readFromEndOfStream())
  Source.fromPublisher(publisherStationBoards) //.map(_.message().toUpperCase)
    .to(Sink.foreach({
      m =>
        stationBoardMasterActor ! m.message()
    })).run()

  val publisherPositions = kafka.consume(ConsumerProperties(
    brokerList = "192.168.99.100:9092",
    zooKeeperHost = "192.168.99.100:2181",
    topic = "cff_train_position",
    groupId = "cff_streaming_akka",
    decoder = new TrainCFFPositionDecoder()
  ).readFromEndOfStream())
  Source.fromPublisher(publisherPositions) //.map(_.message().toUpperCase)
    .to(Sink.foreach({
      m =>
        positionMasterActor ! m.message()
    })).run()

  override def receive: Receive = {
    case m: StationBoardDetails => stationBoardMasterActor forward (m)
    case StationBoardsSnapshot => stationBoardMasterActor forward (StationBoardsSnapshot)
    case PositionSnapshot => positionMasterActor forward PositionSnapshot
    case x => log.warning(s"lost message $x")
  }
}

object MainActor {
  implicit val actorSystem = ActorSystem("cff-streaming")
  implicit val materializer = ActorMaterializer()

  lazy val mainActor = actorSystem.actorOf(Props[MainActor], "main")

  def apply() = mainActor
}