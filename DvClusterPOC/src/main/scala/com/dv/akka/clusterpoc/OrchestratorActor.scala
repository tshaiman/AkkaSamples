
package com.dv.akka.clusterpoc


import akka.actor.{Actor, ActorRef, Props}
import akka.http.scaladsl.server.RouteResult.Complete
import akka.util.Timeout
import com.dv.akka.clusterpoc.models._
import akka.pattern.ask
import scala.concurrent.ExecutionContext.Implicits.global


import scala.concurrent.Await
import scala.concurrent.duration._

object OrchestratorActor {
  def props(clusterService: ActorRef,complete : UrlInfo=>Unit)(implicit numOfCalls: Int): Props = Props(new OrchestratorActor(clusterService,complete, numOfCalls))
}


class OrchestratorActor(clusterService: ActorRef,complete : UrlInfo=>Unit, numOfCalls: Int) extends Actor {
  var counter: Int = 0
  var _msg: DvImpProto = null
  var _avromsg:DvImpression = null
  var _big:DvImpressionHuge = null
  implicit val timeout: Timeout = 1 second


  override def receive: Receive = {

    case evt: DvImpProto  =>
      _msg = evt
      clusterService ! evt

    case evt: DvImpression  =>
      _avromsg = evt
      clusterService ! evt

    case big: DvImpressionHuge =>
      _big = big
      clusterService ! big

    case visitReply: Int =>
      counter += 1
      if (counter < numOfCalls) {
        if(_msg != null)
          clusterService ! _msg
        else if (_avromsg != null)
          clusterService ! _avromsg
        else if (_big != null)
          clusterService ! _big
      }
      else {
        complete(UrlInfo(true,counter))
        context.stop(self)
      }

    case consolidationReply: String =>
      complete(UrlInfo(true,consolidationReply.toInt))
      context.stop(self)


  }


}



class OrchObj(clusterService:ActorRef,complete: UrlInfo=>Unit,numOfCalls:Int) {
  def receive(job:Job): Unit = {
    implicit val timeout:Timeout = Timeout(3 seconds)
    val ctr:Int = if (job.evtType ==1) 7 else 1
    for(i <- 1 to  ctr) {
      val f = (clusterService ? job)
      Await.result(f,2.seconds)
    }
    complete(UrlInfo(true,ctr))
  }
}