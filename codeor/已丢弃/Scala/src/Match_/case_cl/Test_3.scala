package Match_.case_cl
import java.util
import java.util.{HashMap => javaMap}

object Test_3 {
    def main(args: Array[String]): Unit = {
        var map : javaMap[String,Int] = new javaMap[String,Int]();
        map.put("李四",12);
        map.put("王五",12);
        map.put("沥青",13);

        var map1 = Map[String,Int]();
        map1 += "李四" -> 12;
        map1 += "王五" -> 12;
        map1 += "沥青" -> 13;
        for((k,12) <-  map1){
            println(k);
        }
    }
}
