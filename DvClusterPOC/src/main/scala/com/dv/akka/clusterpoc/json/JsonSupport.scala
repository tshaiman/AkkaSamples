package com.dv.akka.clusterpoc.json

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.dv.akka.clusterpoc.models.{UrlInfo,Error}
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val errorFormat = jsonFormat1(Error)
  implicit val urlInfoFormat = jsonFormat2(UrlInfo)
}
