package classall

object Test2 {
    def main(args: Array[String]): Unit = {
        var e = new run(5,"2");
        println(e.a.getClass.getTypeName);
        println(e.b.getClass.getTypeName);
    }
    class run[T,V](var a : T,var b : V){
    }

}
