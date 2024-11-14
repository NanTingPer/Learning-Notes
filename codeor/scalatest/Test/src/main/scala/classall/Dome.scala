package classall

import java.io.{BufferedWriter, File, FileWriter}
import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.io.Source

object Dome {
    def main(args: Array[String]): Unit = {
        //定义数据源
        var source = Source.fromFile("src/main/scala/classall/1.txt");
        //读取所有数据
        var string: String = source.mkString;
        //分割
        var list1 : List[String] = string.split("""\s+""").toList;
        //转Int
        var list2 : List[Int] = list1.map((f : String) =>{f.toInt});
        //去重 排序
        list2 = list2.toSet.toList.sorted;
        //创建写入
        var bw = new BufferedWriter(new FileWriter("src/main/scala/classall/2.txt"));
        //写入
        list2.foreach((f:Int)=>{
            bw.write(f.toString);
            bw.newLine();
            bw.flush();
        })

        bw.close();
    }


    /*//泛型 列表 IO流
    def Outputfile[T >: File](file : T) ={
        var files = file.asInstanceOf[File];
        Source.fromFile(files).mkString;
    }
    def listTypeTolistType[T,V](array: Array[T],arrayBu : ArrayBuffer[V]):ArrayBuffer[V] = {
        if(array.size == 0){
            return new ArrayBuffer[V]();
        }
        try {
            if (array(0).getClass.getTypeName.toString == "String"){
                var arrayBuffer = new ArrayBuffer[Int]()
                array.foreach((a:T)=>{
                    arrayBuffer += a.asInstanceOf[Object].toString.toInt;
                })
            }
            return arrayBu;
        }
    }

    def main(args: Array[String]): Unit = {
        //获取文件
        var file = new File("src/main/scala/classall/1.txt");
        var fileString = Outputfile(file);
        var arr = fileString.split("""\s+""");
        var arrInt : ArrayBuffer[Int] = new ArrayBuffer[Int];
        arrInt = listTypeTolistType[String,Int](arr,arrInt);
        arrInt.foreach((f : Int) =>{
            println(f);
        })
    }*/

//    def main(args: Array[String]): Unit = {
//        //关联源文件
//        var file = new File("src/main/scala/classall/1.txt");
//        //获得源文件的迭代器
//        var InterString = Source.fromFile(file).getLines();
//        //定义变长列表 存储内容
//        var list : ListBuffer[String] = new ListBuffer[String];
//        while (InterString.hasNext){
////            var reg = """\s+""".r;
////            if(reg.findAllMatchIn(InterString.next()).size == 0){
//                list += InterString.next();
////            }
//        }
//        //去重
//        var set = list.toSet;
//        list = new ListBuffer[String];
//        list ++= set.toList;
//        var listInt:ArrayBuffer[Int] = new ArrayBuffer[Int]();
//        for (elem <- list) {
//            listInt += elem.toInt;
//        }
//
//        listInt = listInt.sorted;
////        list = new ListBuffer[String]();
////        listInt.foreach((e : Int) =>{
////            list+=e.toString;
////        })
//
//        import java.io._
//        //创建文件输出流
//        file = new File("src/main/scala/classall/2.txt");
//        file.createNewFile();
//        var bos : BufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
//        var arrayByte : ArrayBuffer[Byte] = new ArrayBuffer[Byte]();
////        list.foreach((f:String) =>{
////            arrayByte += f.toInt.toByte;
////        })
//        listInt.foreach((f : Int)=>{
//            arrayByte += f.toByte;
//        })
//        var arrayBytes : Array[Byte] = arrayByte.toArray;
//        arrayBytes.foreach((s : Byte)=>{
//            println(s);
//        })
//        bos.write(arrayBytes);
//
//        bos.close();
//    }
}
