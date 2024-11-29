package Tuple_List.ListtoList

object sortby {
  def main(args: Array[String]): Unit = {
    var listst : List[String] = List("08老王 09老朱 03老侯 04老民 05劳心 07劳烦 04劳累");
    var list2 : List[String] = listst.flatMap((a : String)=>{
      a.split(" ");
    })
    var list3 = list2.sortBy[Int]((a : String)=>{
      var stb : StringBuilder = new StringBuilder();
      stb += a.charAt(0);
      stb += a.charAt(1);
      stb.toInt;
    })

    print(list3);
  }
}
