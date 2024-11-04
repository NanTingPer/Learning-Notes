package com.example

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object WorkerMain
{
    def main(args: Array[String]): Unit =
    {
        var WorkerSystem = ActorSystem.create("WorkerSystem", ConfigFactory.load());
        var workers = WorkerSystem.actorOf(Props(Workers),"workers");
    }
}
