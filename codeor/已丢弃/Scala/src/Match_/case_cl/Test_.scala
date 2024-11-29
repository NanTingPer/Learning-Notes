package Match_.case_cl

import Match_.case_cl.case_class._

object Test_ {
    def main(args: Array[String]): Unit = {
        var yl01 : Any = new Cust("李四",12);

        //模式匹配
        yl01 match {
            case Cust(name,age) if(age == 12)=> println("Cust类型 12岁");
            case Ord(id) => printf("Ord类型")
            case _ => println("不匹配");
        }
    }
}
