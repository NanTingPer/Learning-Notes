package case_ClassorObj

object Test02_obj {
  def main(args: Array[String]): Unit = {
    var stu : Student = new Student("李四",14,Man)
    print(stu.toString)
  }

  trait Sex{}
  case object Man extends Sex() {
  }
  case object WoMan extends Sex(){
  }

  case class Student(var name : String , var age : Int , var sex : Sex)
  {

  }
}
