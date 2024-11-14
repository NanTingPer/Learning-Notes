package Traversable_s

object forall_ {
    def main(args: Array[String]): Unit = {
        var t1 = Traversable(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17);
        println(t1.forall((a:Int) =>{
            a % 2 == 0;
        }));
        println(t1.exists((a:Int)=>{
            a % 2 == 0;
        }))
    }
}
