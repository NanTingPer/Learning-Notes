package Func

object klh {
    def main(args: Array[String]): Unit = {
        var str : String = "xyz";
        var str2 : String = "abc";
        str = klh.str(str,str2)((str,str2) =>{
            str + str2;
        })
        println(str);
    }

    def str(s1 : String,s2:String)(s3 : (String,String)=>String) = s3(s1,s2);
}
