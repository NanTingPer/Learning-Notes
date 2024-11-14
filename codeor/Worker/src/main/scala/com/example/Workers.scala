package com.example

import akka.actor.Actor
import java.io._

object Workers extends Actor
{
    case class emuAddtoWorker(id : Int,cpu : Int,memory : Int);
    override def receive: Receive =
    {
        case a => {
            var oos = new ObjectOutputStream(System.out);
            oos.writeObject(a);
            var ois = new ObjectInputStream(System.in);
            ois.
            println(b);
        }
    }
}
