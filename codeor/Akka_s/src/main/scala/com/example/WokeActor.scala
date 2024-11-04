package com.example

import akka.actor.Actor

object WokeActor extends Actor
{
    override def receive: Receive =
    {
        case "setp" =>
        {
            println("Woke : System发了setp");
            val recActor = context.actorSelection("akka://actorSystem@127.0.0.1:7666/user/recActor");
            recActor ! "cconect"
        }
        case "cconect" =>
        {
            println("Woke : 收到回传信息! cconect")
        }
    }
}
