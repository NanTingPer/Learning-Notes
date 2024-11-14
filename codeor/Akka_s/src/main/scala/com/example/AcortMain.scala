package com.example

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object AcortMain
{
    def main(args: Array[String]): Unit =
    {
        //akka://actorSystem@198.18.0.1:25520
        var aectorSystem = ActorSystem("actorSystem", ConfigFactory.load());
        var wokeActor = aectorSystem.actorOf(Props(WokeActor), "wokeActor");
        var e = 0;
        wokeActor ! "setp"

    }
}
