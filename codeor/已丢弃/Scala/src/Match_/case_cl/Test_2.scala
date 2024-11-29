package Match_.case_cl

object Test_2{
    def main(args: Array[String]): Unit = {
        var array1 = (0 to 10).toArray;
        var Array(_,x,y,z,_*) = array1;
        println(x,y,z)
    }
}
