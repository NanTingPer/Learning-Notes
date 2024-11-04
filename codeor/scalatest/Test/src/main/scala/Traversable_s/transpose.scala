package Traversable_s

object transpose {
    def main(args: Array[String]): Unit = {
        var t1 : Traversable[Traversable[Int]] = Traversable(Traversable(1,4,7),Traversable(2,5,8),Traversable(3,6,9));
        t1 = t1.transpose;
        println(t1);
    }
}
