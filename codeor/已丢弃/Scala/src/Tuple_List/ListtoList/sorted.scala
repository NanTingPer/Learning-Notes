package Tuple_List.ListtoList

object sorted {
  def main(args: Array[String]): Unit = {
    var list1 : List[Int] = List(1,2,3,4,5,6,65,5,4,63,46,3,64,3,347,3,7);
    var list2 = list1.sorted;
    println("升序: " + list2);
    print("降序: " + list2.reverse);
  }
}
