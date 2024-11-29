package Tuple_List

object List_union {
  def main(args: Array[String]): Unit = {
    var list1 : List[Int] = List(1,2,3,4);
    var list2 : List[Int] = List(1,2,3,4,5);
    list1 = list1.union(list2);
    print(list1);
  }
}
