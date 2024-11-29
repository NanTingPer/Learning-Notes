package Match_.case_cl

object Test_5 {
    def main(args: Array[String]): Unit = {
        var sum : PartialFunction[Int,String] = {
            case 1 =>"一" ;
            case 2 => "二";
            case 3 => "三";
            case _ => "无";
        }

        println(sum(1));
        println(sum(2));
        println(sum(4));
    }
}
