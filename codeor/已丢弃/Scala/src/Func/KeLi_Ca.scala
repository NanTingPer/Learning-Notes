package Func

object KeLi_Ca {
    def main(args: Array[String]): Unit = {
        def sum(num1:Double,num2:Double)(fun : (Double,Double) => Double) = fun(num1,num2)
        var a1 = 1.2;
        var a2 = 1.3;
        var sum1 = sum(a1,a2)((a1,a2) => a1 + a2);
        println(sum1)
    }


}
