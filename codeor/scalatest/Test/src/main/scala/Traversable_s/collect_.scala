package Traversable_s

object collect_ {
    def main(args: Array[String]): Unit = {
        var t1 = (1 to 10).toTraversable;
        var pl : PartialFunction[Int,Int] = {
            case num if num % 2 == 0 => num;
        }
        t1 = t1.collect(pl);
        println(t1);
    }

}
