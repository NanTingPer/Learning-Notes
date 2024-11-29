package Tuple_List.ListtoList

import scala.collection.mutable.ListBuffer

object fold_ {
    def main(args: Array[String]): Unit = {
        var listst : ListBuffer[String] = ListBuffer("老王","老朱","老侯","老民","劳心","劳烦" ,"劳累");
        listst += listst.fold("姓名集:")((e : String, e1 : String) => {
            e + " " +  e1;
        })
        listst = listst.drop(listst.size - 1);
        print(listst)
    }
}
