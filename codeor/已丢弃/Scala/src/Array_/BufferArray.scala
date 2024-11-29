package Array_

import scala.collection.mutable.ArrayBuffer

object BufferArray {
  def main(args: Array[String]): Unit = {
    var bufferArrayname : ArrayBuffer[String] = new ArrayBuffer[String]();
    var Arrayname : Array[String] = new Array[String](3);
    Arrayname(0) = "王五";
    Arrayname(1) = "张三";
    Arrayname(2) = "老虎";
    bufferArrayname += "花猫";
    bufferArrayname --= Arrayname;
    print(bufferArrayname.toString());
  }
}
