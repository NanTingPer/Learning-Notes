package Tuple_List.ListtoList

import scala.collection.mutable.ListBuffer

object Dome {
    def main(args: Array[String]): Unit = {
        //分别为 学生姓名 语文 数学 英语
        var list : ListBuffer[Tuple4[String,Int,Int,Int]] = new ListBuffer[Tuple4[String,Int,Int,Int]]();
        list += Tuple4("张三",37,90,100);
        list += Tuple4("李四",90,73,81);
        list += Tuple4("王五",60,90,76);
        list += Tuple4("赵六",59,21,72);
        list += Tuple4("田七",100,100,100);
        //要求获取语文大于60(含)
        list = list.filter((p:(String,Int,Int,Int)) =>{
            p._3 >= 60;
        })

        var listadd:ListBuffer[(String,Int)] = new ListBuffer[(String,Int)]();
        list.foreach((f:(String,Int,Int,Int)) =>{
            listadd += Tuple2(f._1,(f._2+f._3+f._4));
        })

        //按照总成绩降序排列
        listadd = listadd.sortBy((e : (String,Int)) => {
            e._2
        })

        //降序
        listadd = listadd.reverse;
        print(listadd)
    }
}
