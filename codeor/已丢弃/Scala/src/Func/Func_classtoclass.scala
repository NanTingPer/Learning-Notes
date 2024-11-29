package Func

object Func_classtoclass {
    def main(args: Array[String]): Unit = {
        var a = (a :Int) =>{"*" * a}
        var list = (1 to 10).toList;
        var list2 = list.map(a);
        println(list2);
    }
}
