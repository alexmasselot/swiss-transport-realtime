import play.sbt.PlayImport._
import com.typesafe.sbt.packager.docker._
import play.sbt.routes.RoutesKeys._

enablePlugins(DockerPlugin)

enablePlugins(JavaAppPackaging)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

//
// http://spark.apache.org/docs/latest/quick-start.html#a-standalone-app-in-scala
//
name := """spark-streaming-position"""
organization := "ch.octo"
version := "0.1.0"
scalaVersion := Version.scala
maintainer := "amasselot@octo.com"

libraryDependencies ++= Dependencies.sparkAkkaHadoop

libraryDependencies ++= Seq(
  cache,
  filters,
  "com.typesafe.play" %% "play-json" % "2.5.0",
  "com.github.tototoshi" %% "scala-csv" % "1.3.0",
  "com.github.nscala-time" %% "nscala-time" % "2.10.0"
)


scalaSource in Compile := baseDirectory.value / "src/main/scala"


releaseSettings

scalariformSettings

initialCommands in console :=
  """
    |import org.apache.spark._
    |import org.apache.spark.streaming._
    |import org.apache.spark.streaming.StreamingContext._
    |import org.apache.spark.streaming.dstream._
    |import akka.actor.{ActorSystem, Props}
    |import com.typesafe.config.ConfigFactory
    | """.stripMargin

//mappings in Docker := mappings.value

packageName in Docker := s"${organization.value}/${name.value}"

dockerBaseImage := "java:8"

dockerExposedPorts := Seq()

mappings in Universal += {
  val conf = baseDirectory.value / "conf" / "application-docker.conf"
  conf -> "conf/application.conf"
}

routesGenerator := InjectedRoutesGenerator
