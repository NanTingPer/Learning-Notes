package Spark_RDD.Lasting;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.ArrayList;

public class cache_
{
    public static void main(String[] args)
    {
        SparkConf conf = new SparkConf();
        conf.setMaster("local[*]");
        conf.setAppName("Lasting");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> mapRDD = sc
            .textFile("src/main/java/Spark_RDD/Spark.txt")
             .map(str ->
             {
                 System.out.println("执行");
                 return str;
             });
        mapRDD.cache();

        mapRDD.groupBy(str -> str)
            .collect();

        System.out.println("#######################");
        mapRDD.flatMap(str -> {
            ArrayList<String> liste = new ArrayList<>();
            liste.add(str);
            return liste.iterator();
        }).collect();


        sc.close();
    }
}
