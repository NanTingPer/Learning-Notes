package Tuple_List

object List_zip {
  def main(args: Array[String]): Unit = {
    var list:List[String] = List("张三","李四");
    var listage : List[Int] = List(24,23);
    var listnameAndage : List[(String,Int)] =  list.zip(listage);

    var Tuple : Tuple2[List[String],List[Int]] = listnameAndage.unzip;
    var listName : List[String] = Tuple._1;
    print(listName)
  }
}
