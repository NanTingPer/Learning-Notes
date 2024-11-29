package Tuple_List.ListtoList

object filter {
  def main(args: Array[String]): Unit = {
    var listst : List[String] = List("老王 老朱 老侯 老民","劳心 劳烦 劳累 老贼");
    var list2 = listst.flatMap((a : String) =>{
      a.split(" ");
    })

    var list3 = list2.filter((a : String) =>{
      a.charAt(0).equals('老');
    })
    print(list3);
  }
}
