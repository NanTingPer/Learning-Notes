package com.example

import akka.actor.Actor

object RecActor extends Actor
{
    override def receive: Receive =
    {
        case "cconect" =>
        {
            println("RecActor : 收到数据cconect");
            sender ! "cconect";
        }
    }
}
