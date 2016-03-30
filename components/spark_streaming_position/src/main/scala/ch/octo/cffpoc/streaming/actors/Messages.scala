package ch.octo.cffpoc.streaming.app.akka.actors

/**
 * Created by alex on 30/03/16.
 */
object Messages {

  case object GetGlobalStats

  case class StationBoardDetails(stopId: Long)

}
