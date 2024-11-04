package Traversable_s

import scala.collection.generic.CanBuildFrom
import scala.collection.mutable.ListBuffer
import  scala.collection.generic.CanBuildFrom
import scala.collection.generic.ParFactory

object zipWithIndex {
    def main(args: Array[String]): Unit = {
        var list : ListBuffer[String] = new ListBuffer[String];
        list += "张三";
        list += "李四";
        list += "王五";
        list += "赵六";
        list += "田七";

        var iterable = list.toIterable;
        //def zipWithIndex[A1 >: A, That](implicit bf: CanBuildFrom[Repr, (A1, Int), That]): That
        var iterable2 = iterable.zipWithIndex.map((f:(String,Int)) =>{
            (f._2,f._1);
        });
        println(iterable2)
    }
}
