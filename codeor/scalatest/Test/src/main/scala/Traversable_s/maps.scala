package Traversable_s

import scala.collection.mutable
import scala.concurrent.Future
import scala.io.StdIn

object maps {
    def main(args: Array[String]): Unit = {
        //先获取数据
        println("请输入数据:");
        var str = StdIn.readLine();
        //创建Map集合
        var map = mutable.HashMap[Char,Int]();
        for(s <- str){
            if (map.contains(s)){
                var e = map(s);
                map -= s;
                map += s -> (e+1);
            }else{
                map += s -> 1;
            }
        }

        for((k,v) <- map){
            println(k.toString + v)
        }
        var a = e;
        var w:Future[Any] = Actor
        w.isS
    }

    case class e(var name : String="",var age :Int=1){

    }
}
