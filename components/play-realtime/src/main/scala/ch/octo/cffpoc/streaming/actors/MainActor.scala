package ch.octo.cffpoc.streaming.app.akka.actors

import javax.inject._

import akka.actor._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{ Sink, Source }
import ch.octo.cffpoc.streaming.actors.PositionMasterActor
import ch.octo.cffpoc.streaming.app.akka.actors.Messages.{ PositionDetails, PositionSnapshot, StationBoardDetails, StationBoardsSnapshot }
import ch.octo.cffpoc.streaming.serialization.{ StationBoardEventDecoder, TrainCFFPositionDecoder }
import com.softwaremill.react.kafka.{ ConsumerProperties, ReactiveKafka }
import play.api.Configuration

/**
 * Created by alex on 30/03/16.
 */

class MainActor(configuration: Configuration) extends Actor with ActorLogging {

  implicit val actorSystem = ActorSystem("ReactiveKafka")
  implicit val materializer = ActorMaterializer()

  val stationBoardMasterActor = context.actorOf(Props[StationBoardMasterActor], "station-board")
  val positionMasterActor = context.actorOf(Props[PositionMasterActor], "position")

  val kafka = new ReactiveKafka()

  val kafkaUrl = configuration.getString("broker.in.kafka.host").get + ":" + configuration.getString("broker.in.kafka.port").getOrElse(9092)
  val zookeeperUrl = configuration.getString("broker.in.zookeeper.host").get + ":" + configuration.getString("broker.in.zookeeper.port").getOrElse(2181)

  log.info(s"listening to kafka $kafkaUrl")
  log.info(s"listening to zookeeper $zookeeperUrl")

  val publisherStationBoards = kafka.consume(ConsumerProperties(
    brokerList = kafkaUrl,
    zooKeeperHost = zookeeperUrl,
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
    brokerList = kafkaUrl,
    zooKeeperHost = zookeeperUrl,
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
    case d: PositionDetails => positionMasterActor forward (d)
    case x => log.warning(s"lost message $x")
  }
}

@Singleton
class MainActorGenerator @Inject() (configuration: Configuration, actorSystem: ActorSystem) {
  val mainActor = actorSystem.actorOf(Props(classOf[MainActor], configuration))
}

//object MainActor {
//  implicit val actorSystem = ActorSystem("cff-streaming")
//  implicit val materializer = ActorMaterializer()
//
//  //
//  //
//  lazy val mainActor = actorSystem.actorOf(Props(classOf[MainActor]))
//
//  def apply()(implicit configuration: Configuration) = mainActor
//}