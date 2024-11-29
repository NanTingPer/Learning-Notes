package Is_or_As

object isOras{
  def main(args: Array[String]): Unit = {
    var e: Proe = new Student;
    print(e.isInstanceOf[Student] + "\r\n")
    var e1 = e.asInstanceOf[Student];
    e1.runTwo();
  }
}
