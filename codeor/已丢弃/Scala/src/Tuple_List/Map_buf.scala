package Tuple_List

object Map_buf {
  def main(args: Array[String]): Unit = {
    var map : Map[Int,String] = Map[Int,String]();
    map+= 1 -> "王五";
    map+= 2 ->"张三";
    for((key,value) <- map)
      {
        print(key,value)
      }
  }
}
