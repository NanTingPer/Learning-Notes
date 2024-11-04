package Spark_RDD.KVClass;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;


import scala.Tuple2;


import java.util.Arrays;
import java.util.*;

public class kvgtoupBy
{
    public static void main(String[] args)
    {
        SparkConf conf = new SparkConf();
        conf.setMaster("local");
        conf.setAppName("KVClass");

        //构建Spark运行环境
        JavaSparkContext jsc = new JavaSparkContext(conf);

        List<Integer> intList = Arrays.asList(1,2,3,4,5,7);

        //获取RDD
        jsc.parallelize(intList)
           .groupBy(num -> num % 2).mapValues(num ->{
               Iterator<Integer> iterator = num.iterator();
               int numsub = 0;
               while (iterator.hasNext())
               {
                   numsub += iterator.next();
               }
               return numsub;
           })
          .collect().forEach(System.out::println);

        //释放资源
        jsc.close();
    }
}
