package ch.octo.cffpoc.streaming.app.actors

import akka.actor.Actor
import ch.octo.cffpoc.position.{ TrainPositionSnapshot, TrainCFFPositionLatestCollection }
import ch.octo.cffpoc.stationboard.StationBoardsSnapshotStats
import ch.octo.cffpoc.stops.{ StopCloser, StopCollection }
import ch.octo.cffpoc.streaming.serialization.serializers
import org.apache.kafka.clients.producer.{ ProducerRecord, KafkaProducer }
import org.apache.spark.streaming.receiver.ActorHelper
import org.joda.time.DateTime
import play.api.libs.json.Json
import serializers._
import scala.collection.JavaConversions._

/**
 * Created by alex on 15/03/16.
 */
class TrainPositionAggregatorActor(kafkaProducerParams: Map[String, Object], kafkaProduceTopic: String) extends Actor with ActorHelper {
  var latestPosition = TrainCFFPositionLatestCollection()

  val producer = new KafkaProducer[String, String](kafkaProducerParams)

  val stops = StopCollection.load("src/main/resources/stops.txt", true)
  val stopCloser = new StopCloser(stops, 300);

  override def preStart() = {
    println("=== TPAggregatorActor is starting up ===")
    println(s"=== path=${context.self.path} ===")
  }

  /*
   * output the snapshot to TSV format, to limit the transfered size
   */
  def cvsify(snapshot: TrainPositionSnapshot) = {
    "train_id\ttrain_category\ttrain_name\ttrain_lastStopName\tposition_lat\tposition_lng\n" +
      snapshot.positions.values.map({
        p =>

          s"${p.train.id}\t${p.train.category}\t${p.train.name.trim()}\t${p.train.lastStopName}\t${p.timedPosition.position.lat}\t${p.timedPosition.position.lng}"
      }).mkString("\n")
  }

  def receive = {
    //         store() method allows us to store the message so Spark Streaming knows about it
    //         This is the integration point (from Akka's side) between Spark Streaming and Akka
    case tplist: TPList =>
      latestPosition = latestPosition + tplist.positions
      log.info(s"added ${tplist.positions.size} positions / ${latestPosition.size}")

      val timestamp = DateTime.now()
      //        tplist.positions match {
      //        case Nil if latestPosition.size == 0 => DateTime.now()
      //        case Nil => latestPosition.toList.map(_.current.timedPosition.timestamp).maxBy(_.getMillis)
      //        case xs =>
      //          val t = tplist.positions.last.current.timedPosition.timestamp
      //          latestPosition = latestPosition.after(t.minusSeconds(60))
      //          t
      //      }

      val snapshot = latestPosition.snapshot(timestamp).closedBy(stopCloser)
      val messageStr = cvsify(snapshot) //Json.toJson(snapshot).toString()
      val message = new ProducerRecord[String, String](kafkaProduceTopic,
        null,
        messageStr) //Json.toJson(snapshot).toString())
      log.info(s"$kafkaProduceTopic <- (${messageStr.length}) ${messageStr.substring(0, 25)}...")
      producer.send(message)

    case x => log.warn(s"unmatched message ${x.toString.take(100)}")
  }
}
