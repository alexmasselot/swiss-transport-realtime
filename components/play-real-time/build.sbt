import play.sbt.PlayImport._
import play.sbt.routes.RoutesKeys._

//
// http://spark.apache.org/docs/latest/quick-start.html#a-standalone-app-in-scala
//
name         := "play-real-time"
organization := "com.octo.suisse"
version      := "0.1"
scalaVersion := Version.scala

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaSource in Compile <<= baseDirectory / "src/scala"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"

libraryDependencies ++= Dependencies.sparkAkkaHadoop
libraryDependencies ++= Seq(
  specs2 % Test
)

dependencyOverrides ++= Set(
  "com.typesafe.akka" %% "akka-actor" % "2.4.1",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.4"
)

releaseSettings

scalariformSettings

initialCommands in console := """
  |import org.apache.spark._
  |import org.apache.spark.streaming._
  |import org.apache.spark.streaming.StreamingContext._
  |import org.apache.spark.streaming.dstream._
  |import akka.actor.{ActorSystem, Props}
  |import com.typesafe.config.ConfigFactory
  |""".stripMargin


