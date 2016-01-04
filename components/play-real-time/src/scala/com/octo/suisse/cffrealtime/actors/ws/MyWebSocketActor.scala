package com.octo.suisse.cffrealtime.actors.ws

/**
 * Created by alex on 31/12/15.
 */
import akka.actor._
import com.octo.suisse.cffrealtime.streaming.TrainPositionKafkaStream

object MyWebSocketActor {
  def props(out: ActorRef) = Props(new MyWebSocketActor(out))
}

class MyWebSocketActor(out: ActorRef) extends Actor {

  out ! "BORDEL"
  //  TrainPositionStream.stream.foreachRDD(rdd =>
  //    rdd.foreach(x => out ! x.toString())
  //  )
  def receive = {
    case msg: String =>
      out ! ("I received your message: " + msg)
    case x => out ! s"GOTCH undefined $x"
  }
}
