import java.io.File
import scala.io.Source

object Main {

  //定义一个类 用来实现原没有的方法
  class toRed(file : File){
    def read = Source.fromFile(file).mkString;
  }

  //定义一个单例对象 用于静态调用
  object refile {
    implicit def fietored(file: File) = new toRed(file);
  }

  def main(args: Array[String]): Unit = {
    //手动导入
    import refile.fietored
    var file = new File("src/main/scala/implicit_/内容.txt");
    println(file.read);
  }
}