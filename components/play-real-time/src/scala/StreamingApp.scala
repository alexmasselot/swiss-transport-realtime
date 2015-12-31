import akka.actor.{ Actor, Props }
import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.receiver._

class Helloer extends Actor with ActorHelper {
  override def preStart() = {
    println("")
    println("=== Helloer is starting up ===")
    println(s"=== path=${context.self.path} ===")
    println("")
  }
  def receive = {
    // store() method allows us to store the message so Spark Streaming knows about it
    // This is the integration point (from Akka's side) between Spark Streaming and Akka
    case s => store(s)
  }
}

object StreamingApp {
  def main(args: Array[String]) {
    // Configuration for a Spark application.
    // Used to set various Spark parameters as key-value pairs.
    val driverPort = 7777
    val driverHost = "localhost"
    val conf = new SparkConf(false) // skip loading external settings
      .setMaster("local[*]") // run locally with as many threads as CPUs
      .setAppName("Spark Streaming with Scala and Akka") // name in web UI
      .set("spark.logConf", "true")
      .set("spark.driver.port", driverPort.toString)
      .set("spark.driver.host", driverHost)
      .set("spark.akka.logLifecycleEvents", "true")
    val ssc = new StreamingContext(conf, Seconds(1))
    val actorName = "helloer"

    val kafkaStream = KafkaUtils.createStream(ssc,
      "192.168.99.100:2181", "duh", Map("cff_train_position" -> 50))

    kafkaStream.print()

  }
}
