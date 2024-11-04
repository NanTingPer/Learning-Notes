package implicit_

import scala.collection.mutable.ListBuffer

object Dome {
    class List_sum[A](list : ListBuffer[A]){
        def list_zip() = {
            list(0).getClass.getTypeName match {
                case int => {
                    var e : ListBuffer[Int] = new ListBuffer[Int];
                    list.foreach((f : A) => {
                        e += f.asInstanceOf[Int];
                    })
                    var sum = 0;
                    e.foreach((f: Int) => {sum += f})
                    Some(sum);
                }
                case _ => None;
            }
        }
    }

    class List_avg(list: List[Int]){
        var listbu:ListBuffer[Int] = new ListBuffer[Int]();
        listbu ++= list;
        def avg ={
            listbu.size match{
                case 0 => None;
                case _ => Some(list.sum / list.size);
            }
        }
    }

    object ListbuToList_sum {
        implicit def listbutolistsum(list : ListBuffer[Int]) = {
            new List_sum[Int](list);
        }

        implicit def listAvg(list: List[Int]) ={
            new List_avg(list);
        }
    }
    def main(args: Array[String]): Unit = {
        import ListbuToList_sum._;
        var list : ListBuffer[Int] = new ListBuffer[Int]();
        list ++= (0 to 20).toList;
        println(list.list_zip());

        var list2:List[Int] = (0 to 20).toList;
        println(list2.avg);
    }
}
