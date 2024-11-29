package Match_

import scala.io.StdIn

object match_num {
    def main(args: Array[String]): Unit = {
        println("请输入:");
        var str = StdIn.readLine();

        str = str match{
            case "你好" => "我很好";
            case "你" => "好";
            case _ => "没选项";
        }

        println(str);
    }
}
