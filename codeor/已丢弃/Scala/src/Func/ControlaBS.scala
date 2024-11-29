package Func

object ControlaBS {
    def main(args: Array[String]): Unit = {
        var funct = (fun01 : () => Unit) =>{
            println("开始");
            fun01();
            println("结束");
        }
        funct(() =>{
            println("1");
            println("2");
            println("3");
            println("4");
            println("8");
            println("12");
        })
    }
}
