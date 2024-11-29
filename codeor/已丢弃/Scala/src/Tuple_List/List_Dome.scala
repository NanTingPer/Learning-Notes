package Tuple_List

import java.util

object List_Dome {
  def main(args: Array[String]): Unit = {
//    var list : List[Int]= List(1,2,3,4,5,6);

//    var list : List[Int]= Nil;
    var list : util.ArrayList[Int] = new util.ArrayList[Int];
    list.size();
    list.add(1);
    print(list.get(list.size()-1))
  }
}
