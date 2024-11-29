package Tuple_List.ListtoList

object flatMap {
  def main(args: Array[String]): Unit = {
    var listst : List[String] = List("老王 老朱 老侯 老民","劳心 劳烦 劳累");
    var list2 : List[Array[String]] = listst.map((name : String) => {
      name.split(" ");
    })
    var list3 : List[String] = list2.flatten;
    println(list3);
    //扁平化
    var list4 = listst.flatMap((f : String)=>{
      f.split(" ");
    })
    print(list4);
  }
}
