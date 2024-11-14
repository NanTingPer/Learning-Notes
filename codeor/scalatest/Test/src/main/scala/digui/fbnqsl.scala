package digui

object fbnqsl {

    //斐波那契数列
    //已知数列 1 1 2 3 5 8 13 21
    //第三个月开始 数字就是前面两数之和
    def show(num : Int): Int = {
        if(num == 1 || num == 2) {
            return 1;
        }else{
            return (num - 1) + show(num - 2);
        }
    }

    def main(args: Array[String]): Unit = {
        println(show(12));
    }
}
