package Regex_

import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex

object regex_email_list {
    def main(args: Array[String]): Unit = {
        var listm : ListBuffer[String] = new ListBuffer[String]();
        listm += "38219283@qq.com";
        listm += "adwa2846@gmail.com";
        listm += "zhansan@163.com";
        listm += "123waf.com";

        var regex: Regex = """.+@(.+)\..+""".r;

        var list2 = listm.map{
            case x @ regex(r) => x -> r;
            case x => x -> "未匹配";
        }
        print(list2);

//        var list2 = listm.map((f:String) => {
//            if (regex.findAllMatchIn(f).size !=0) {
//                Some(f)
//            }else{
//                None
//            }
//        })

//        listm = listm.filter((p:String) => {
//            !(regex.findAllMatchIn(p).size == 0);
//        })
//
//        listm = listm.map((s : String) =>{
//            s.split("@")(1)
//        })

//        print(list2);
    }
}
