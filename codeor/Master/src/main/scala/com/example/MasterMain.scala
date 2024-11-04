package com.example

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.io.Source

object MasterMain
{
    //7666
    def main(args: Array[String]): Unit =
    {
        val source = Source.fromFile("C:/LiMGren/codeor/Spark/src/main/java/Spark_RDD/Items.hjson");
        println(source.mkString)
//
//        var MasterSystem = ActorSystem.create("MasterSystem", ConfigFactory.load());
//        var Master = MasterSystem.actorOf(Props(Masters), "Master");
    }
}
