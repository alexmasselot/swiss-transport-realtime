import sbt._

object Version {
  val akka = "2.4.2"
  val reactiveKafka = "0.8.5"
  val hadoop = "2.7.2"
  val logback = "1.1.3"
  val mockito = "1.10.19"
  val scala = "2.11.7"
  val scalaTest = "2.2.5"
  val slf4j = "1.7.6"
  val spark = "1.6.0"
  val akkaStream = "2.0.3"
}

object Library {
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % Version.akka
  val akkaTestKit = "com.typesafe.akka" %% "akka-testkit" % Version.akka
  val hadoopClient = "org.apache.hadoop" % "hadoop-client" % Version.hadoop
  val logbackClassic = "ch.qos.logback" % "logback-classic" % Version.logback
  val mockitoAll = "org.mockito" % "mockito-all" % Version.mockito
  val scalaTest = "org.scalatest" %% "scalatest" % Version.scalaTest
  val slf4jApi = "org.slf4j" % "slf4j-api" % Version.slf4j
  val sparkStreaming = "org.apache.spark" %% "spark-streaming" % Version.spark
  val sparkStreamingKafka = "org.apache.spark" %% "spark-streaming-kafka" % Version.spark
  val reactiveKafka  = "com.softwaremill.reactivekafka" %% "reactive-kafka-core" % Version.reactiveKafka
  //  val sparkStreamingAkka = "org.apache.spark"  %% "spark-streaming-akka" % Version.spark
}

object Dependencies {

  import Library._

  val sparkAkkaHadoop = Seq(
    sparkStreaming,
    sparkStreamingKafka,
    akkaActor,
    akkaTestKit,
    hadoopClient,
    reactiveKafka,
//    sparkStreamingAkka,
    logbackClassic % "test",
    scalaTest % "test",
    mockitoAll % "test"
  )
}
