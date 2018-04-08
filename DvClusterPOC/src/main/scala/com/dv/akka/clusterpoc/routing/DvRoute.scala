package com.dv.akka.clusterpoc.routing

import java.util.concurrent.atomic.{AtomicInteger, AtomicLong}

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.server.Directives.{get, path, _}
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.dv.akka.clusterpoc.{OrchObj, OrchestratorActor}
import com.dv.akka.clusterpoc.json.JsonSupport
import com.dv.akka.clusterpoc.models._

import scala.concurrent.duration._
import scala.util.Random

trait DvRoute extends JsonSupport {

  val clusterService: ActorRef
  def actorSys: ActorSystem
  def nServices: Int
  val pr1 = getDvProto1()
  val pr2 = getDvProto1()
  val pr3 = getDvProto2()
  val pr4 = getDvProto2()


  val dvRoute: Route = {
    path("setWork") {
      get {
        parameters('time) { time =>
          completeWith(implicitly[ToResponseMarshaller[String]]) { f =>
            clusterService ! SetWork(time.toInt)
            f("OK")
          }
        }
      }
    } ~
    path("parse") {
      implicit val timeout = Timeout(1.seconds)
      implicit val numOfCalls:Int = nServices

      get {
        parameters('url) { url =>
          completeWith(implicitly[ToResponseMarshaller[UrlInfo]]) { f =>
            //actorSys.actorOf(OrchestratorActor.props(clusterService, f)) ! getDvBig()
            val orcObj = new OrchObj(clusterService,f,nServices)
            orcObj.receive(Job(url,getEvtType()))
          }
        }
      }
    }
  }
  val ctr:AtomicLong = new AtomicLong(0)
  val imp11 = Some(DvImpression11())
  val imp12 = Some(DvImpression12())

  def getMsg():DvImpression = {
    val current = ctr.getAndIncrement();
    val evtType = if (current % 5 ==0) 1 else 0
    if(current == Long.MaxValue) ctr.set(0)
    DvImpression(evtType,imp11,None,imp12,None)
  }

  def getMsgProto() : DvImpProto = {
    DvImpProto(pr1,pr2,pr3,pr4,getEvtType())
  }

  def getEvtType():Int = {
    val current = ctr.getAndIncrement();
    val t = if (current % 5 ==0) 1 else 0
    if(current == Long.MaxValue) ctr.set(0)
    t
  }

  def getDvProto1(): Option[DvImpProto1] = {
    val s:String = "abcdefghijklm"
    val r = DvImpProto1(s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s)
    Some(r)
  }

  def getDvProto2(): Option[DvImpProto2] = {
    val s:Int = 0xFFFFFFFF
    Some(DvImpProto2(s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s))
  }

  def getDvBig(): DvImpressionHuge = {
    val arr:Array[Byte] = new Array[Byte](1850)
    val bStr : _root_.com.google.protobuf.ByteString = _root_.com.google.protobuf.ByteString.copyFrom(arr)
    DvImpressionHuge(getEvtType(),bStr)
  }

}


