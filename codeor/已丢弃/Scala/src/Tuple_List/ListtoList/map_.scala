package Tuple_List.ListtoList

import scala.collection.mutable.ListBuffer

object map_ {
  def main(args: Array[String]): Unit = {
    var list1 : List[Int] = List(1,2,3,4,5,6);

    var list2 = list1.map(f = (a: Int) => {
      "*" * a;
    })  :TraversableOnce[String]
    println(list2);

    var list3 = list1.map((in : Int) =>{
      var stb :  StringBuffer = new StringBuffer();
      for(i <- 1 to in){
        stb.append("*");
      }
      stb.toString();
    });
    print(list3)



  }
}
