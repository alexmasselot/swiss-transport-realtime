package ch.octo.cffpoc.streaming.app

import com.typesafe.config.Config
import org.apache.kafka.clients.producer.ProducerConfig

/**
 * Created by alex on 15/03/16.
 */
class KafkaConnector(appConfig: Config, configKey: String, defaultConsumeGroupId: String) {
  def getAppConfOrElse(path: String, default: String): String = if (appConfig.hasPath(path)) {
    appConfig.getString(path)
  } else {
    default
  }

  lazy val consumerParams = Map(
    "zookeeper.connect" -> (getAppConfOrElse("zookeeper.host", "localhost") + ":" + getAppConfOrElse("zookeeper.port", "2181")),
    "group.id" -> getAppConfOrElse(s"kafka.$configKey.consume.group.id", defaultConsumeGroupId)
  )

  lazy val kafkaProducerParams = {
    Map(
      ProducerConfig.BOOTSTRAP_SERVERS_CONFIG ->
        (getAppConfOrElse("kafka.host", "localhost") + ":" + getAppConfOrElse("kafka.port", "2181")),
      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG ->
        "org.apache.kafka.common.serialization.StringSerializer",
      ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG ->
        "org.apache.kafka.common.serialization.StringSerializer"
    )
  }
}
