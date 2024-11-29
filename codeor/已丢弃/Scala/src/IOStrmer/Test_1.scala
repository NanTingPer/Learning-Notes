package IOStrmer

import scala.collection.mutable.ListBuffer
import scala.io.Source

object Test_1 {
    def main(args: Array[String]): Unit = {
        var sou = Source.fromFile("src/IOStrmer/res.text","UTF-8");
        var souinter = sou.getLines();
        var list = new ListBuffer[String]();
        while(souinter.hasNext) {
            list += souinter.next();
        }
        print(list)
        sou.close();
    }
}
