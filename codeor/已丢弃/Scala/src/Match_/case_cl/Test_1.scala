package Match_.case_cl

//数组匹配
object Test_1 {
    def main(args: Array[String]): Unit = {
        var array1 = Array(1,2,3,4);
        ppei(array1);
    }

    def ppei(array: Array[Int]) = {
        array match {
            case Array(1,x,y) => println("长度为3 开头为1");
            case Array(1,_*) => printf("数组");
            case _ => printf("不匹配");
        }
    }
}
