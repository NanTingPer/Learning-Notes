package Spark_RDD.其他;

import scala.Tuple2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Item_json01
{
    public static void main(String[] args) throws Exception
    {
        //读取文件
        BufferedReader bis = new BufferedReader(new FileReader("src/main/java/Spark_RDD/Items.hjson"));
        StringBuffer str = new StringBuffer();
        String line = "";

        //读取并存入字符
        while((line = bis.readLine()) != null)
        {
            str.append(line);
        }
        line = str.toString();


        //分割
        String[] split = line.split("[{}]");

        //创建集合
        List<String> strlist = new ArrayList<>();
        //将分割后的数据存入集合
        for(String s : split)
        {
            strlist.add(s.trim());
        }

        //创建键值集
        HashMap<String,String> ItemCount = new HashMap<>();

        for(int i = 0;i < strlist.size() - 1;i +=2)
        {
            ItemCount.put(strlist.get(i),strlist.get(i+1));
        }

        //查看结果
//        ItemCount.forEach((k,v) -> System.out.println(k + v));

        HashMap<String, Tuple2<String,String>> ItemName = new HashMap<>();
        //遍历
        ItemCount.forEach((k,v)->{
            String Name = "";
            String Tool ="";
            int a =0;
            String[] split1 = v.split("[DisplayName:Tooltip:]");
            //得不为空
            for (String s : split1)
            {
                if(!s.equals("") && a == 0)
                {
                    Name = s;
                    a ++;
                } else if (!s.equals("") && a==1)
                {
                    Tool = s;
                }
            }
            Tuple2<String,String> Name_Tool = new Tuple2<>(Name.trim(),Tool.trim());
            ItemName.put(k,Name_Tool);
        });


        Map<String,Tuple2<String,String>> Name_Item = new HashMap<>();
        //遍历key和value内的'''将其杀掉
        ItemName.forEach((k,v) ->
        {
            String key = "";
            String Name ="";
            String Tool ="";

            key = k.replaceAll(":","").trim();
            Name = deldet_t(v._1.replaceAll("'''","").trim());
            Tool = deldet_t(v._2.replaceAll("'''","").trim());

/*            key = get(k,":");
            Name = get(v._1,"'''");
            Name = deldet_t(Name);
            Tool = get(v._2,"'''");
            Tool = deldet_t(Tool);*/

            Name_Item.put(key,new Tuple2<>(Name,Tool));
        });

        Name_Item.forEach( (k,v)->{
            System.out.println(k + v);
        });
    }

    static String get(String str,String regexs)
    {
        if(str.equals("")) return str;
        String[] split = str.split("[" + regexs +"]");
        for (String s : split)
        {
            if(!s.equals(""))
            {
                return s.trim();
            }
        }
        return str;
    }

    static String deldet_t(String str)
    {
        String s = str.replaceAll("\t", "<br>");
        String s1 = s.replaceAll("<br><br>", "<br>");
        return s1.trim();
    }
}
