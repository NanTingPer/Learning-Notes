object Function {
  def main(args: Array[String]): Unit = {
    var num : Int = numAdd(10,20);//30
    print(num + "\n");
    val fun = numAdd _;//转换
    print(fun + "\n");
  }
  //val定义的是常量
  val numAdd = (a : Int,b : Int) => {
    a + b;
  }

  //方法转函数
  def numAdd_ (a : Int,b : Int) : Int  = {
    return a - b;
  }
}
