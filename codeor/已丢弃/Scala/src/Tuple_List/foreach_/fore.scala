package Tuple_List.foreach_

object fore {
  def main(args: Array[String]): Unit = {
    var map : Map[Int,String] = Map[Int,String]();
    map += 1 -> "王五";
    map += 2 -> "李四";
    map += 3 -> "老朱";

//    map.foreach(( kn : (Int , String)) => {
//      println(kn)
//    })
    map.foreach(println(_));

  }

}
