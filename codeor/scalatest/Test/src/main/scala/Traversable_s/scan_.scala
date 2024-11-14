package Traversable_s

object scan_ {
    def main(args: Array[String]): Unit = {
        //求阶乘
        var t1 = (1 to 5).toTraversable;

        var t2 = t1.scan(1)((e :Int,b : Int) =>{
            e * b;
        })
        println(t2);
    }
}
