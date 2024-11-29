package Trait_.trait_member

trait er {
  //具体字段 初始化就是具体
  var name : String = "";
  //抽象字段 不初始化就是抽象
  var age : Int;

  //给出具体执行就是具体方法
  def getname() : String  = {
    return this.name;
  }

  def setage(age : Int) : Unit;
}
