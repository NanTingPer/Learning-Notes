package IOStrmer
import  java.io._
object Test_3 {
    class Student(var name:String,var age:Int) extends Serializable{
    }

    def main(args: Array[String]): Unit = {
        var stu = new Student("利好",23);
        var oos = new ObjectOutputStream(new FileOutputStream("src/IOStrmer/numt6.txt"));
        oos.writeObject(stu);
        oos.close();

        //反序列化
        var ois =new ObjectInputStream(new FileInputStream("src/IOStrmer/numt6.txt"));
        var stu2 : Student = ois.readObject().asInstanceOf[Student];
        ois.close();
        print(stu2.name + stu2.age);
    }
}
