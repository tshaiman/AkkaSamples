package com.dv.akka.clusterpoc
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import com.dv.akka.clusterpoc.routing.DvRoute

import scala.concurrent.ExecutionContext


class HttpServer(port:Int,services:Int)
      (implicit actorSystem: ActorSystem, mat: Materializer, ec: ExecutionContext)
  extends DvRoute {

  override def actorSys: ActorSystem = implicitly
  //implicit val executionContext: ExecutionContext = actorSys.dispatcher
  override def nServices:Int = services
  val clusterService:ActorRef = actorSys.actorOf(Props[ClusterService], name="clusterService")
  val route: Route = dvRoute

  def start(): Unit = {
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    Http().bind("0.0.0.0", 8080).runForeach(_.handleWith(Route.handlerFlow(route)))

  }

}
