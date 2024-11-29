package Tuple_List

object pin_Tuple {
  def main(args: Array[String]): Unit = {
    var tupl1 : Array[(String,Int)] = new Array[(String, Int)](2);

    var tupl : Array[Tuple2[String,Int]]  = new Array[(String, Int)](2);
    tupl(0) = ("王五",14);
    tupl (0)._1;
    print(tupl(0).toString());
  }
}
