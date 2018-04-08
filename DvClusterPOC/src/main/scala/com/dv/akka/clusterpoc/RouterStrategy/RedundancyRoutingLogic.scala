package com.dv.akka.clusterpoc.RouterStrategy

import akka.actor.ActorSystem
import akka.dispatch.Dispatchers
import akka.routing._
import com.typesafe.config.Config

import akka.dispatch.Dispatchers
import akka.routing.Group
import akka.routing.Router
import akka.japi.Util.immutableSeq
import com.typesafe.config.Config

import scala.collection.immutable
import scala.collection.parallel.immutable


class RedundancyRoutingLogic() extends RoutingLogic {
  val roundRobin = RoundRobinRoutingLogic()

  def select(message:Any, routees: scala.collection.immutable.IndexedSeq[Routee]): Routee = {
    val targets = (1 to 1).map(_ ⇒ roundRobin.select(message, routees))

    routees.foreach(rt=>{
      val r = routees.head.asInstanceOf[ActorSelectionRoutee]
      println(r)
      println(r.selection.getClass)

    })
    SeveralRoutees(targets)
  }
}

final case class RedundancyGroup(routeePaths: scala.collection.immutable.Iterable[String]) extends Group {

  def this(config: Config) = this(
    routeePaths = immutableSeq(config.getStringList("routees.paths")))

  override def paths(system: ActorSystem): scala.collection.immutable.Iterable[String] = routeePaths

  override def createRouter(system: ActorSystem): Router =
    new Router(new RedundancyRoutingLogic())

  override val routerDispatcher: String = Dispatchers.DefaultDispatcherId
}