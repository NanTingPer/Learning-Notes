package Traversable_s

import scala.util.Random

object fill_ {
    def main(args: Array[String]): Unit = {
//        var t1 = Traversable.fill(10)("Leng");
//        println(t1);
//
//        var t2 = Traversable.fill(10)(Random.nextInt(12) + 3);
//        println(t2);
//
//        var t3 = Traversable.fill[List[(String,String)]](5)(List(("itcast","itcast")));
//        println(t3);

        //iterate[T](开始值,长度)((f : T)=>{
        //      f => 会作为初始值
        // })
//        var t1 = Traversable.iterate[Int](1,10)((f : Int)=>{
//            f * 10;
//        })

        //range[类型 : 必须为Math(数学)](start : 第一个值,end : 最后一个值) : CC[T]
        //range[类型 : 必须为Math(数学)](start : 第一个值,end : 最后一个值 , 隐式) : CC[T]
        //隐式默认为1 => 间隔多少截取
        //def range[T: Integral](start: T, end: T): CC[T] = range(start, end, implicitly[Integral[T]].one)
        var t1 = Traversable.range[Int](0,601,50);
        println(t1);
    }
}
