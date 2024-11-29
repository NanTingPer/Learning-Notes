package Tuple_List.ListtoList

//分组
object groupBy_ {
    def main(args: Array[String]): Unit = {
        //创建元组集
        var list1 = List("老王" -> "女","老卢" -> "男","老铭"-> "男","老胜" -> "女","老阿" -> "女");
        var list2 = list1.groupBy((f : (String,String)) =>{
            f._2; //按照字段二进行分组
        })
        //输出列表内容
        println(list2);
        //获取各人数
        print("男: " + list2.get("男").get.size + "  " + "女: " + list2.get("女").get.size);
    }
}