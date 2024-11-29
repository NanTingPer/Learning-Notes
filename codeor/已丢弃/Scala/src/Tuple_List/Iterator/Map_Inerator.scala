package Tuple_List.Iterator

object Map_Inerator {
  def main(args: Array[String]): Unit = {
    var map : Map[Int,String] = Map[Int,String]();
    map += 1 -> "王五";
    map += 2 -> "李四";
    map += 3 -> "老朱";
    var inter : Iterator[(Int,String)]  = map.iterator;
    while(inter.hasNext){
      print(inter.next());
    }
  }
}
