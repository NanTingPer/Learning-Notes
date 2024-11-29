package IOStrmer

import java.io.{BufferedWriter, FileInputStream}
import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import java.io._
import scala.io.Source

object Test_4 {
    def main(args: Array[String]): Unit = {
      /*
        //先读取
        var sou = Source.fromFile("src/IOStrmer/Student.txt");
        var st = sou.mkString;
        var list = st.split("""\s+""");
        var sb = new StringBuilder();
        var listst = new ListBuffer[String];

        list.map((f:String)=>{
            try{
                f.toInt
                sb ++= f;
                sb ++= " ";
            }catch {

                case e:Exception => {
                    sb ++= f;
                    listst += sb.toString();
                    sb = new StringBuilder();
                }
            }
        })
        print(listst.length)
        for(e <- listst)
        {
             print(e)
        }
        */
        var list = ListBuffer[Student]();
        //读取文件
        var sou = Source.fromFile("src/IOStrmer/Student.txt");
        var int = sou.getLines();
        while(int.hasNext){
            var st =int.next();
            var strings : Array[String] = st.split("""\s+""");
            list+= new Student(strings(0),strings(1).toInt,strings(2).toInt,strings(3).toInt);
        }
        list.sortWith((s1 : Student ,s2:Student)=>{
            s1.add() > s2.add();
        })
        print(list);

        var file = new File("src/IOStrmer/stu.txt");
        file.createNewFile();
        var fos = new BufferedOutputStream(new FileOutputStream(file));
        var arr : ArrayBuffer[Byte] = new ArrayBuffer[Byte]();
        list.foreach((f:Student) =>{
            var sb = new StringBuilder();
            sb ++= "姓名: " ;sb ++= f.name.toString;
            sb ++= "语文成绩: ";sb++=f.Chinese.toString;
            sb ++= "数学成绩: ";sb++=f.math.toString;
            sb ++="英语成绩: ";sb++=f.English.toString;
            sb ++= "\r\n";
            arr ++=sb.toString.getBytes();
        })
        fos.write(arr.toArray);
        fos.flush();
        fos.close();
        sou.close();
    }

    case class Student(var name : String,var Chinese:Int,var math:Int,var English:Int){
        def add(): Int = {
            Chinese + math + English
        }
    }
}
