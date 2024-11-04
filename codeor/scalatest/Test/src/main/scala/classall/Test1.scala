package classall

object Test1 {
    def main(args: Array[String]): Unit = {
        var list = List[Int](10);
        list = (0 to 10).toList;
        println(midder(list));

    }
    def midder[T](list : List[T]) ={
        list(list.length/2);
    }

}
