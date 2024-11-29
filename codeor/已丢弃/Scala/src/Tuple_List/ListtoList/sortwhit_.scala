package Tuple_List.ListtoList

import scala.util.control.Breaks
import scala.util.control.Breaks._

object sortwhit_ {
  def main(args: Array[String]): Unit = {
    var listst : List[String] = List("08老王 09老朱 03老侯 04老民 05劳心 07劳烦 04劳累");
    var list2 : List[String] = listst.flatMap((a : String) => {
      a.split(" ");
    })
    var list3 : List[String] = list2.sortWith((a,b)=>{
      //去除空格
      var a1 = a.trim;
      var b1 = b.trim;

      //变长字符串 减轻内存占用
      var stba : StringBuilder = new StringBuilder();
      var stbb : StringBuilder = new StringBuilder();
      Breaks.breakable {
        //截取字符串a
        for (i <- 0 to a1.length) {
          if (pdnum(a1.charAt(i))) {
            stba += a1.charAt(i);
          } else {
            Breaks.break();
          }
        }
      }
      Breaks.breakable {
        //截取字符串b
        for(i <- 0 to b1.length){
          if(pdnum(b1.charAt(i))){
            stbb += b1.charAt(i);
          }else{
            Breaks.break();
          }
        }
      }

      //容错代码
      if(stba.size == 0){
        stba +='0';
      }
      if(stbb.size ==0){
        stbb += '0';
      }

      //最终决定
      if(stba.toString().toInt < stbb.toString().toInt) {
        true;
      } else {
        false;
      }
    })
    print(list3);
  }

  //定义一个方法 判断字符是否为数字
  def pdnum(chare : Char): Boolean = {
    if(chare.equals('0') || chare.equals('1') || chare.equals('2') || chare.equals('3') ||
        chare.equals('4') || chare.equals('5') || chare.equals('6') || chare.equals('7') ||
        chare.equals('8') || chare.equals('9') ){
      return true;
    }else{
      return false;
    }
  }
}