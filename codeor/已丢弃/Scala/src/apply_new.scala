object apply_new {
  class Student(var name : String,var age : Int)
  {
    override def toString: String = {
      return name + " " +  age;
    }
  }
  //伴生对象
  object Student
    {
      //定义apply方法
      def apply(name : String,age : Int): Student = {
        new Student(name, age);
      }
    }

  def main(args: Array[String]): Unit = {
    var  stu : Student = Student("li",12);
    print(stu.toString);
  }
}
