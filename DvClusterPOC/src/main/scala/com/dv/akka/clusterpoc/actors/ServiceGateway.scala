package com.dv.akka.clusterpoc.actors

import java.util.UUID

import akka.actor.{Actor, ActorRef, Props}
import akka.routing.FromConfig
import com.dv.akka.clusterpoc.models._

object ServiceGateway {
  def props()(implicit workMicrosec:Int): Props = Props(new ServiceGateway(workMicrosec))
}

class ServiceGateway(workMicrosec:Int)  extends Actor {

  //The Worker Router
  val router:ActorRef = context.actorOf(FromConfig.props(UrlWorker.props()), "workerRouter")


  def receive = {
    ///Management API to set sleeep times
    case sw: SetWork =>
      println(s"Node $self got Set-Work time request ${sw.workMicro} microsec")
      UrlWorker.workTime = sw.workMicro

    case job: DvImpression =>
      router.tell(job, sender)
    case job:Job =>
      router.tell(job,sender)

    case job: DvImpProto =>
      router.tell(job, sender)

    case job: DvImpressionHuge =>
      router.tell(job, sender)

  }
}
