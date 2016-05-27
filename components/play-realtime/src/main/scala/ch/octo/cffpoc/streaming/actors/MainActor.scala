package ch.octo.cffpoc.streaming.app.akka.actors

import javax.inject._

import akka.actor._
import akka.kafka.ConsumerSettings
import akka.kafka.scaladsl.Consumer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import ch.octo.cffpoc.streaming.actors.PositionMasterActor
import ch.octo.cffpoc.streaming.app.akka.actors.Messages._
import ch.octo.cffpoc.streaming.serialization.{ StationBoardEventDeserializer, TrainCFFPositionDeserializer }
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import play.api.Configuration

/**
 * Created by alex on 30/03/16.
 */

class MainActor(configuration: Configuration) extends Actor with ActorLogging {

  implicit val actorSystem = ActorSystem("ReactiveKafka")
  implicit val materializer = ActorMaterializer()

  var stationBoardMasterActor: ActorRef = _
  var positionMasterActor: ActorRef = _

  //val kafka = new ReactiveKafka()

  val kafkaUrl = configuration.getString("kafka.host").get + ":" + configuration.getString("kafka.port").getOrElse(9092)
  //val zookeeperUrl = configuration.getString("zookeeper.host").get + ":" + configuration.getString("zookeeper.port").getOrElse(2181)

  log.info(s"listening to kafka $kafkaUrl")

  //log.info(s"listening to zookeeper $zookeeperUrl")

  //
  //
  //  val publisherPositions = kafka.consume(ConsumerProperties(
  //    brokerList = kafkaUrl,
  //    zooKeeperHost = zookeeperUrl,
  //    topic = "cfftrainposition",
  //    groupId = "cff_streaming_akka",
  //    decoder = new TrainCFFPositionDecoder()
  //  ).readFromEndOfStream())
  //  Source.fromPublisher(publisherPositions) //.map(_.message().toUpperCase)
  //    .to(Sink.foreach({
  //      m =>
  //        positionMasterActor ! m.message()
  //    })).run()

  override def preStart = {

    stationBoardMasterActor = context.actorOf(Props[StationBoardMasterActor], "station-board")
    positionMasterActor = context.actorOf(Props[PositionMasterActor], "position")

    log.info("launching cffstationboard pipe")
    val consumerSettingsStationBoard = ConsumerSettings(actorSystem, new ByteArrayDeserializer, new StationBoardEventDeserializer,
      Set("cffstationboard"))
      .withBootstrapServers(kafkaUrl)
      .withGroupId("cff_streaming_akka_stationboard")

    Consumer.committableSource(consumerSettingsStationBoard.withClientId("cff_streaming_akka_stationboard"))
      .runWith(Sink.foreach(msg => stationBoardMasterActor ! msg.value))
    log.info("launched cffstationboard pipe")

    log.info("launching cfftrainposition pipe")
    val consumerSettingsPosition = ConsumerSettings(actorSystem, new ByteArrayDeserializer, new TrainCFFPositionDeserializer(),
      Set("cfftrainposition"))
      .withBootstrapServers(kafkaUrl)
      .withGroupId("cff_streaming_akka_position")
    Consumer.committableSource(consumerSettingsPosition.withClientId("cff_streaming_akka_position"))
      .runWith(Sink.foreach(msg => positionMasterActor ! msg.value))
    log.info("launched cfftrainposition pipe")

  }

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
  lazy val mainActor = actorSystem.actorOf(Props(classOf[MainActor], configuration))
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