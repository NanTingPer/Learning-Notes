package com.example

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object AcortMain
{
    case class 发送(var txt : String){}
    case class 回传(var txt : String){}
    def main(args: Array[String]): Unit =
    {
        var aectorSystem = ActorSystem("actorSystem", ConfigFactory.load());
        var recActor = aectorSystem.actorOf(Props(RecActor), "recActor");
    }
}
