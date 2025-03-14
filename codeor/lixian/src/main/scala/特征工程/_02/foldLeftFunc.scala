package 特征工程._02

object foldLeftFunc {
    def main(args: Array[String]): Unit = {
        val nameArray : Array[String] = Array[String]("力宏", "立群", "白沙", "白鹤", "玫瑰")
        //a 是 柯里化左边的类型，a1是被操作集合的类型
        //柯里化函数左边的值，代表计算的默认值，可以在这个值的基础上进行函数内计算操作

        //foldLeft右边的函数，其中a是上一轮计算完成后的值

        val str  = "名字集"
        val r: Any = nameArray.foldLeft(str)((str: String, a1: String) => {
            a1 + str
        })
        println(r)
    }
}
