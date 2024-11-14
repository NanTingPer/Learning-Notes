package implicit_

import java.io.File
import scala.io.Source

object Test1 {
    class Fileread(file : File){
        def read = Source.fromFile(file).mkString;
    }
    def main(args: Array[String]): Unit = {
        implicit def filere(file : File) = { new Fileread(file)}

        var file : File = new File("src/main/scala/implicit_/内容.txt");

        println(file.read);
    }
}
