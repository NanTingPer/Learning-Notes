package ClassAS

object run {
  def main(args: Array[String]): Unit = {
    var stu : Person = new Student();
    print(stu.getClass);
    //转换类型  需要有继承关系
    var stu1 = stu.asInstanceOf[Student];
    //获取类型  classOf[]
    //判断类型
    print("\n" + classOf[Student].equals(classOf[Student]));
    print("\n" + stu.getClass);
  }

}
