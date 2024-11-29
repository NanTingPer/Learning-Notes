package IOStrmer

import scala.io.Source

object Test_2 {
    def main(args: Array[String]): Unit = {
        var sou = Source.fromFile("src/IOStrmer/numt");
        var st = sou.mkString;//获取文件内全部内容
        var sts:Array[String] = st.split("\\s+");
        var stsint = sts.map((f:String) =>{
            f.toInt;
        })
        for(i <- 0 to stsint.length - 1){
            println(stsint(i));
        }
    }
}
