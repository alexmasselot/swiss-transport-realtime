package ch.octo.cffpoc.streaming.app

import akka.actor.{ ActorSystem, Props }
import ch.octo.cffpoc.position.TrainCFFPosition
import ch.octo.cffpoc.stationboard.StationBoardEvent
import ch.octo.cffpoc.stops.{ StopCloser, StopCollection }
import ch.octo.cffpoc.streaming.app.actors.{ SBEventList, StationBoardAggregatorActor, TPList, TrainPositionAggregatorActor }
import ch.octo.cffpoc.streaming.{ StationBoardEventDecoder, TrainCFFPositionDecoder }
import kafka.serializer.StringDecoder
import org.apache.spark.SparkEnv
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka._

/**
 * Created by alex on 17/02/16.
 */
object StreamLatestApp extends StreamingApp {
  val kafkaConsumeGroupId = "stream_latest_position_app"

  /**
   * reads cff train positions and send them to a condensator actor
   * thisactor will beuild a snpashot abd send it back to kafka
   *
   * @param sparkStreamingContext
   * @param actorSystem
   */
  def pipeTrainPositions(sparkStreamingContext: StreamingContext, actorSystem: ActorSystem) = {
    val kafkaConnector = new KafkaConnector(appConfig, "train_position", "stream_latest_position_app")
    logger.info(s"kafka consumer params: ${kafkaConnector.consumerParams}")

    val actorName = "position-aggregator"
    val props = Props(new TrainPositionAggregatorActor(
      kafkaConnector.kafkaProducerParams,
      getAppConfOrElse("kafka.train_position.produce.topic", "train_position_snapshot")
    ))
    val aggregActor = actorSystem.actorOf(props, actorName)

    val lines = KafkaUtils.createStream[String, TrainCFFPosition, StringDecoder, TrainCFFPositionDecoder](
      sparkStreamingContext,
      kafkaConnector.consumerParams,
      List((getAppConfOrElse("kafka.train_position.consume.topic", "cff_train_position"), numThreads)).toMap,
      StorageLevel.MEMORY_AND_DISK_SER
    )
      .map(_._2)

    val closer = new StopCloser(StopCollection.load(getClass.getResourceAsStream("/stops.txt")), 500)
    lines
      .foreachRDD({
        rdd =>
          val n = rdd.count()
          aggregActor ! TPList(rdd.collect())
      })
  }

  /**
   * get stationboars as a flow and accumlate it in and actor to be flushed regularly
   *
   * @param sparkStreamingContext
   * @param actorSystem
   */
  def pipeStationBoards(sparkStreamingContext: StreamingContext, actorSystem: ActorSystem) = {
    val kafkaConnector = new KafkaConnector(appConfig, "station_board", "stream_latest_stationboard_app")
    logger.info(s"kafka consumer params: ${kafkaConnector.consumerParams}")

    val actorName = "stationboard-aggregator"
    val props = Props(new StationBoardAggregatorActor(
      kafkaConnector.kafkaProducerParams,
      getAppConfOrElse("kafka.station_board.produce.topic", "station_board_snapshot")
    ))
    val aggregActor = actorSystem.actorOf(props, actorName)

    val events = KafkaUtils.createStream[String, StationBoardEvent, StringDecoder, StationBoardEventDecoder](
      sparkStreamingContext,
      kafkaConnector.consumerParams,
      List((getAppConfOrElse("kafka.station_board.consume.topic", ""), numThreads)).toMap,
      StorageLevel.MEMORY_AND_DISK_SER //MEMORY_AND_DISK_SER_2
    )
      .map(_._2)

    events //.window(Seconds(10), Seconds(4))
      .foreachRDD({
        rdd =>
          val n = rdd.count()
          aggregActor ! SBEventList(rdd.collect().toList)
      })

  }

  def main(args: Array[String]) {

    val sparkStreamingContext = initSparkStreamingContext

    //sparkStreamingContext.checkpoint("/tmp/streaming-slpa-checkpoint")
    logger.info(s"appConfig= $appConfig")

    pipeTrainPositions(sparkStreamingContext, SparkEnv.get.actorSystem)
    pipeStationBoards(sparkStreamingContext, SparkEnv.get.actorSystem)

    sparkStreamingContext.start()
    sparkStreamingContext.awaitTermination()
  }
}
