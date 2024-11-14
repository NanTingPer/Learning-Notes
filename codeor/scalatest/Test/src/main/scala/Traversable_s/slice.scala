package Traversable_s

object slice {
    def main(args: Array[String]): Unit = {
//        var t1 = (0 to 20).toList.toTraversable;
        var t1 = Traversable(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17);
        var index = 0;
        var a = t1.toList.find((a : Int) =>{
            if(a % 5 == 0){
                true;
            }
            else {
                index += 1;
                false;
            }
        })
        println(a);

        var t2 = t1.slice(index,t1.size);
        println(t2);

    }
}
