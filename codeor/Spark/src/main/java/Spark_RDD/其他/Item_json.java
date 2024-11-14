package Spark_RDD.其他;

import java.util.*;
import java.io.*;

public class Item_json
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
        String[] split = line.split("[:}]");

        //创建集合
        List<String> strlist = new ArrayList<>();

        //将分割后的数据存入集合
        for(String s : split)
        {
            strlist.add(s);
        }

        //遍历集合 删除多余的"{"
        for(int i =0;i<strlist.size();i++)
        {
            String s = strlist.get(i);
            StringBuffer str1 = new StringBuffer();
            for(int j = 0;j < s.length();j++)
            {
                 if(s.charAt(j) != '{')
                 {
                     str1.append(s.charAt(j));
                 }
            }
            strlist.set(i, str1.toString());
        }

        System.out.println(strlist.get(0));

//        SparkConf conf = new SparkConf();
//        conf.setMaster("local[*]");
//        conf.setAppName("Item_json");
//        JavaSparkContext sc = new JavaSparkContext(conf);

        //对接磁盘数据源



    }
}
