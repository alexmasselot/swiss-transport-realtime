package ch.octo.cffpoc.streaming.app.actors

import akka.actor.Actor
import ch.octo.cffpoc.position.TrainCFFPositionLatestCollection
import ch.octo.cffpoc.stops.{ StopCloser, StopCollection }
import org.apache.kafka.clients.producer.{ ProducerRecord, KafkaProducer }
import org.apache.spark.streaming.receiver.ActorHelper
import org.joda.time.DateTime
import play.api.libs.json.Json
import ch.octo.cffpoc.streaming.serializers._
import scala.collection.JavaConversions._

/**
 * Created by alex on 15/03/16.
 */
class TrainPositionAggregatorActor(kafkaProducerParams: Map[String, Object], kafkaProduceTopic: String) extends Actor with ActorHelper {
  var latestPosition = TrainCFFPositionLatestCollection()

  val producer = new KafkaProducer[String, String](kafkaProducerParams)

  val stops = StopCollection.load("src/main/resources/stops.txt")
  val stopCloser = new StopCloser(stops, 300);

  override def preStart() = {
    println("=== TPAggregatorActor is starting up ===")
    println(s"=== path=${context.self.path} ===")
  }

  def receive = {
    //         store() method allows us to store the message so Spark Streaming knows about it
    //         This is the integration point (from Akka's side) between Spark Streaming and Akka
    case tplist: TPList =>
      latestPosition = latestPosition + tplist.positions
      log.warn(s"added ${tplist.positions.size} positions")

      val timestamp = tplist.positions match {
        case Nil if latestPosition.size == 0 => DateTime.now()
        case Nil => latestPosition.toList.map(_.current.timedPosition.timestamp).maxBy(_.getMillis)
        case xs =>
          val t = tplist.positions.last.current.timedPosition.timestamp
          latestPosition = latestPosition.after(t.minusSeconds(60))
          t
      }

      val snapshot = latestPosition.snapshot(timestamp).closedBy(stopCloser)
      val message = new ProducerRecord[String, String](kafkaProduceTopic,
        null,
        Json.toJson(snapshot).toString()) //Json.toJson(snapshot).toString())
      producer.send(message)

    case x => log.warn(s"unmatched message ${x.toString.take(100)}")
  }
}
