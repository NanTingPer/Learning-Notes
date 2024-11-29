object Method {
  def main(args: Array[String]): Unit = {
    var a : Int = 10;
    var b : Int = 20;
    print(returnAeB(a) + "\n");
    //惰性调用
    //lazy只能修饰 val
    lazy val e : Int = returnAeB(19);
    print(e + "\n");
    printHello;//调用无返回值方法
  }

  //def = 定义
  //a : Int int类型的变量 a
  //:Int 返回值类型
//  def getMaxNum(a : Int, b : Int):Int =
//  {
//    return if(a > b) a else b;
//  }

  //递归求阶乘
  def returnAeB(a : Int): Int = {
    if(a == 1) {
        return 1;
      }
      else{
        return a * returnAeB(a -1);
      }
  }

  //默认参数方法
  def defnum(a : Int = 10,b : Int = 20) : Int ={
    return a + b;
  }

  //变长参数 传入的是数据数组
  def lenmothod(a : Int*) : Int ={
    return a.sum;
  }
  //
  //返回值为Unit
  //直接表示为过程
  def printHello() {
    print("HelloWorld");
  }
}
