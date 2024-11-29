package im_package

import java.util

object Test1 {
  def main(args: Array[String]): Unit = {
//    选取性导包
    import java.util.{ArrayList,HashMap,HashSet}
//    更改名称可避免包冲突
//    import java.util.{ArrayList => List}

    //不导指定包
//    import java.util.{ArrayList => _}
//    import java.util._;
    var list : ArrayList[String] = new ArrayList[String]();
    list.add("lim");
    list.add("rel");
    for(i <- 0 to list.size()-1)
      {
          print(list.get(i) + "\n");
      }
  }
}
