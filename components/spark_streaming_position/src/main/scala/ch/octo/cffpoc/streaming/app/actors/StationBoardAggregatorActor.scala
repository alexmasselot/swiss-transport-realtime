package ch.octo.cffpoc.streaming.app.actors

import akka.actor.Actor
import ch.octo.cffpoc.stationboard.StationBoardsSnapshot
import ch.octo.cffpoc.streaming.serializers._
import org.apache.kafka.clients.producer.{ KafkaProducer, ProducerRecord }
import org.apache.spark.streaming.receiver.ActorHelper
import play.api.libs.json.Json

import scala.collection.JavaConversions._

/**
 * Created by alex on 15/03/16.
 */
class StationBoardAggregatorActor(kafkaProducerParams: Map[String, Object], kafkaProduceTopic: String) extends Actor with ActorHelper {
  var boards = StationBoardsSnapshot()

  val producer = new KafkaProducer[String, String](kafkaProducerParams)

  override def preStart() = {
    println("=== StationBoardAggregatorActor is starting up ===")
    println(s"=== path=${context.self.path} ===")
  }

  def receive = {
    //         store() method allows us to store the message so Spark Streaming knows about it
    //         This is the integration point (from Akka's side) between Spark Streaming and Akka
    case evts: SBEventList =>
      evts.events.foreach(e => boards = boards + e)
      log.info(s"added ${evts.events.size} events")

      if (evts.events.lastOption.isDefined) {
        val t = evts.events.last.timestamp
        log.info("removing: " + boards.before(t.minusMinutes(1)).countAll)
        boards = boards.after(t.minusMinutes(1)).before(t.plusMinutes(15))
      }

      //println(boards.boards.values.map(_.stop.name).toList.sorted)
      val stats = boards.stats
      val messageStr = Json.toJson(stats).toString()

      val message = new ProducerRecord[String, String](
        kafkaProduceTopic,
        null,
        messageStr) //Json.toJson(snapshot).toString())
      log.info(s"$kafkaProduceTopic <- (${messageStr.length})  $stats")
      producer.send(message)

    case x => log.warn(s"unmatched message ${x.toString.take(100)}")
  }
}

