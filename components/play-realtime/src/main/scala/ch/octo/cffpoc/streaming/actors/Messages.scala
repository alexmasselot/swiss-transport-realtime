package ch.octo.cffpoc.streaming.app.akka.actors

/**
 * Created by alex on 30/03/16.
 */
object Messages {

  case object StationBoardsSnapshot

  case class StationBoardDetails(stopId: Long)

  case object PositionSnapshot

  case class PositionDetails(id: String)

}
