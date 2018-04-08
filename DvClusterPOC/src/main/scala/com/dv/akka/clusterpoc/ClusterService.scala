package com.dv.akka.clusterpoc

import akka.actor.{Actor, Props}
import akka.routing.FromConfig
import com.dv.akka.clusterpoc.models._


class ClusterService() extends Actor {
  implicit val workmMicrosec:Int = 0
  // This router is used both with lookup and deploy of routees. If you
  // have a router with only lookup of routees you can use Props.empty
  val workerRouter = context.actorOf(FromConfig.props(Props.empty),name = "workerRouter")

  def receive = {

    case x:DvImpressionHuge =>
      workerRouter forward x
    case j:Job =>
      workerRouter forward j

    case t:SetWork =>
      for( i <-1 to 7)
        workerRouter ! t


  }
}

