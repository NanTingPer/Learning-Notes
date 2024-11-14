package Traversable_s

import com.sun.media.sound.SoftTuning

import scala.collection.mutable.ListBuffer
import scala.util.Random

object Student_ {
    case class Student(var name : String,var age : Int)
    def main(args: Array[String]): Unit = {
        var list : ListBuffer[String] = new ListBuffer[String];
        list += "张三";
        list += "李四";
        list += "王五";
        list += "赵六";
        list += "田七";
        var a = list.size;
        var t1 : Traversable[Student]= Traversable.iterate[Student](new Student("",1),list.size+1)(
            (f : Student)=>{
                a -=1;
                new Student(list(a),Random.nextInt(10) + 10);
            }
        )
        t1 = t1.tail;

        //排序 按照年龄
        var list1 : List[Student] = t1.toList;
        t1 = list1.sortWith((stu : Student,stu2 : Student) =>{
            stu.age > stu2.age;
        }).toTraversable;

        println(t1);
    }
}
