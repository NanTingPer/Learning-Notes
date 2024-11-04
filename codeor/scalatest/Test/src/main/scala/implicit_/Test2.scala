package implicit_

object Test2 {
/*    //定义一个show方法 实现功能
    def show(name : String)(implicit spstr : (String,String)) = {
        spstr._1 + name + spstr._2;
    }

    //定义一个单例对象 决定隐式值
    object spstr_defa{
        implicit var str : (String,String) = ("<<<",">>>");
    }
    def main(args: Array[String]): Unit = {
        //导入隐式值
        import spstr_defa.str;
        println(show("你好"));
    }*/

    //自动导
    def show(name: String)(implicit spiltstr : (String,String)) = {
        spiltstr._1 + name + spiltstr._2;
    }

    def main(args: Array[String]): Unit = {
        implicit var spilt_str:(String,String) = ("<<<" , ">>>");
        println(show("你好"));
    }
}
