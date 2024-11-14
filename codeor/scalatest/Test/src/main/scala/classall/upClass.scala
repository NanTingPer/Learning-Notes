package classall

import scala.collection.mutable.ArrayBuffer

object upClass {
    class Person private{
        var name : String = "";
        var age : Int = 0;
        def this(name : String,age : Int) = {
            this();
            this.name = name;
            this.age = age;
        }
    }
    class Studnet(var a : String,var b : Int) extends Person(a,b){
    }

    def array[T <: Person](e : Array[T]): Unit = {
        for(p <- e){
            println(p);
            println(p.age + " " + p.name)
        }
    }

    def main(args: Array[String]): Unit = {
        var listPer : ArrayBuffer[Person] = new ArrayBuffer[Person];
        listPer += new Person("2",3);
        listPer += new Person("3",4);
        var listBuPer = listPer.toArray;
        array(listBuPer);

        var listStu : ArrayBuffer[Studnet] = new ArrayBuffer[Studnet];
        listStu += new Studnet("2",3);
        listStu += new Studnet("3",4);
        var listBuStu = listStu.toArray;
        array(listBuStu);
    }
}
