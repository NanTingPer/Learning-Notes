package Tuple_List

import scala.collection.mutable.ListBuffer

object List_Buffer {
  def main(args: Array[String]): Unit = {
    var list_Buffer : ListBuffer[Int] = new ListBuffer[Int]();
    list_Buffer += 1;
    print(list_Buffer);
  }
}
