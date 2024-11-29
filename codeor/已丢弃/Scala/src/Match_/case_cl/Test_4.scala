package Match_.case_cl

object Test_4 {
    def chushu(a : Int , b : Int) = {
        if(b == 0){
            None;
        }
        else
        {
            Some(a / b);
        }
    }
    def main(args: Array[String]): Unit = {
        var e = chushu(1,0);
        e match {
            case None => println("除数为0");
            case Some(x) => println(s"结果为: ${x}" )
        }
    }
}
