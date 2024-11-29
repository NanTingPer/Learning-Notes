package case_ClassorObj

object Test01 {

  def main(args: Array[String]): Unit = {
    var stu : Student = new Student("李四",12);
    stu.setName("张三");
    stu.setAge(17);
  }

  case class Student(private var name : String,private var age : Int)
  {
    def setName(name : String): Unit = {
      this.name = name;
    }

    def setAge(age : Int):Unit ={
      this.age = age;
    }

    def setName_Age(name : String,age:Int) : Unit ={
      this.name = name;
      this.age = age;
    }
  }
}
