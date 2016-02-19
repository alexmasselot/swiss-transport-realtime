import play.sbt.PlayImport._
//
// http://spark.apache.org/docs/latest/quick-start.html#a-standalone-app-in-scala
//
name         := """spark-streaming-scala-akka"""
organization := "pl.japila"
version      := "1.0"
scalaVersion := Version.scala

libraryDependencies ++= Dependencies.sparkAkkaHadoop

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.4.2"
)

dependencyOverrides ++= Set(
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
