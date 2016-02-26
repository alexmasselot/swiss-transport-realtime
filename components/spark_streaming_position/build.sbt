import play.sbt.PlayImport._
import com.typesafe.sbt.packager.docker._


enablePlugins(DockerPlugin)

enablePlugins(JavaAppPackaging)

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
  "com.typesafe.play" %% "play-json" % "2.4.2"
)

dependencyOverrides ++= Set(
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.4"
)

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

//dockerfile in docker := {
//  val appDir: File = stage.value
//  val targetDir = "/app"
//
//  new Dockerfile {
//    from("java:8")
//    add("conf/application-docker.conf", s"$targetDir/conf/application.conf")
//    entryPoint(s"$targetDir/bin/${executableScriptName.value}")
//    copy(appDir, targetDir)
//  }
//}
