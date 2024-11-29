package Tuple_List.ListtoList
import scala.collection.immutable._
import scala.collection.mutable.ListBuffer;

object reduce_ {
    def main(args: Array[String]): Unit = {
        var list1 = List("老王" -> "女","老卢" -> "男","老铭"-> "男","老胜" -> "女","老阿" -> "女");
        var list2 = list1.groupBy((f : (String,String)) =>{
            f._2;
        })

        var list3:ListBuffer[(String,String)] = new ListBuffer[(String,String)]();

        for((k,v) <- list2) {
            //表合并
            list3 ++= v;
        }

        var list4:ListBuffer[String] = new ListBuffer[String]();
        list3.foreach((f:(String,String))=>{
            var sb = new StringBuilder();
            sb++=f._1 ++=f._2;
            list4 += sb.toString();
        })

        //聚合
        list4 += list4.reduce((f:String,e:String)=>{
            var sb =new StringBuilder();
            for(i <-f.length-3 to f.length -1){
                sb += f.charAt(i);
            }
            list4 -= sb.toString();
            f + e;
        })

        //另一种删除方法
        list4 = list4.filter((p:String) => {
            if(p.size<=3){
                false;
            }else{
                true;
            }
        })
        print(list4)
    }
}
