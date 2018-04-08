package com.dv.akka.clusterpoc.actors


import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import com.dv.akka.clusterpoc.models.{DvImpProto, DvImpression, DvImpressionHuge, Job}

import scala.concurrent.{Await, ExecutionContext, Future}
import akka.pattern.pipe


object UrlWorker {
  def props(): Props = Props[UrlWorker]
  var workTime:Int = 400 // default unless set
  var consolidationTime: Int = 50
  val workStr:String = "abcdefghijklmnopqrstuvwxyz1234567890"
}

class UrlWorker() extends Actor {

  implicit val ec:ExecutionContext = context.system.dispatchers.lookup("my-thread-pool-dispatcher")

  def receive = {

    case evt: DvImpression if evt.evtType == 1 =>
      workAndReply(sender(),UrlWorker.workTime,2)

    case evt:DvImpression if evt.evtType != 1 =>
      workAndReply(sender(),UrlWorker.consolidationTime,"200")

    case evt:DvImpProto if evt.evtType == 1 =>
      workAndReply(sender(),UrlWorker.workTime,2)

    case evt:DvImpProto if evt.evtType == 0 =>
      workAndReply(sender(),UrlWorker.consolidationTime,"400")

    case evt:DvImpressionHuge if evt.evtType == 1 =>
      workAndReply(sender(),UrlWorker.workTime,2)

    case evt:DvImpressionHuge if evt.evtType == 0 =>
      workAndReply(sender(),UrlWorker.consolidationTime,"600")

    case evt:Job if evt.evtType == 1 =>
      workAndReply(sender(),UrlWorker.workTime,2)

    case evt:Job if evt.evtType == 0 =>
      workAndReply(sender(),UrlWorker.consolidationTime,"800")

  }


  private def workAndReply(replyTo:ActorRef, workInMicro:Int,whatToReply:Any): Unit = {
    Future {
      val end = System.nanoTime() + (workInMicro * 1000)
      while (System.nanoTime() < end) {UrlWorker.workStr.reverse}
      whatToReply
    }.pipeTo(replyTo)

  }


}
