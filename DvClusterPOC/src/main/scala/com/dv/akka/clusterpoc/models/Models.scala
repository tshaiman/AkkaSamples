package com.dv.akka.clusterpoc.models

import com.dv.akka.clusterpoc.models.Models.DVEvent

object Models {
  // Commands: Do this action (potentially harmful)
  trait DVEvent
}


final case class Error(msg:String)
final case class UrlInfo(isValid:Boolean,enhanceId:Int)

case class SetWork(workMicro:Int)
