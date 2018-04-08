package com.dv.akka.clusterpoc

import akka.actor.ActorSystem
import akka.serialization.SerializationExtension
import akka.stream.ActorMaterializer
import com.dv.akka.clusterpoc.actors.ServiceGateway
import com.typesafe.config.ConfigFactory

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._

object Boot {

  def parseWorkerTimeInMicro(args: Array[String]): Int = parseNumeric("-w", args, 400)

  def parseNumServices(args: Array[String]): Int = parseNumeric("-n", args, 7)

  def parseNumeric(command: String, args: Array[String], defaultVal: Int): Int = {
    val index = args.indexOf(command)
    if (index == -1)
      defaultVal
    else
      args(index + 1).toInt
  }


  def main(args: Array[String]): Unit = {


    val mode = if (args.length > 0) args(0).toLowerCase() else "all"
    implicit val workTime = parseWorkerTimeInMicro(args)
    println(s"*****Welcome to TPS Cluster Benchmark . running mode : $mode ********************")

    mode match {
      case "http" => runAsHttp(parseNumServices(args))
      case "cluster" => if (args.length > 1) startup(args.drop(1)) else startup(Seq("2551", "2552"))
      case "all" => {
        startup(Seq("2551", "2552", "0"))
        runAsHttp(parseNumServices(args))
      }
      case _ => throw new Exception("Incorrect mode specifed use http or cluster")
    }
  }

  def startup(ports: Seq[String])(implicit workTimeMicro: Int): Unit = {
    val hostname = java.net.InetAddress.getLocalHost().getHostAddress()
    println(s"cluster startup with $ports ports. hostname ${hostname} . worktime: $workTimeMicro")
    val seeed = """akka.cluster.seed-nodes= ["akka.tcp://ClusterSystem@127.0.0.1:2551","akka.tcp://ClusterSystem@127.0.0.1:2552"]"""

    ports foreach { port =>
      val config = ConfigFactory.parseString(getConfig(hostname, port.toInt))
        .withFallback(ConfigFactory.parseString("akka.cluster.roles = [compute]"))
        .withFallback(ConfigFactory.load())

      implicit val system = ActorSystem("ClusterSystem", config)
      system.actorOf(ServiceGateway.props(), name = s"serviceGateway")
    }
  }


  def runAsHttp(nServices: Int) {
    val hostname = java.net.InetAddress.getLocalHost().getHostAddress()
    println(s"Http Startup with n Services=$nServices. hostname ${hostname}")
    val config = ConfigFactory.parseString(getConfig(hostname, 0))
      .withFallback(ConfigFactory.load())

    implicit val system = ActorSystem("ClusterSystem", config)
    //val serialization = SerializationExtension(system)
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatchers.lookup("my-thread-pool-dispatcher")

    new HttpServer(8080, nServices).start()

    sys.addShutdownHook(() => {
      val future = system.terminate()
      Await.result(future, 120.seconds)
    })
  }

  //akka.remote.artery.canonical.port=$port
  //akka.remote.artery.canonical.hostname=$hostname
  def getConfig(hostname: String, port: Int): String = {
    s"""
      akka.remote.netty.tcp.hostname=$hostname
      akka.remote.netty.tcp.port=$port
      ${getSeedNodeConfig(hostname)}
      """
  }

  def getSeedNodeConfig(hostname: String): String = {
    if (hostname.contains("127.0.0.1"))
      """akka.cluster.seed-nodes= ["akka.tcp://ClusterSystem@127.0.0.1:2551"]"""
    else
      """akka.cluster.seed-nodes= ["akka.tcp://ClusterSystem@172.25.0.43:2551"]"""

  }


}
