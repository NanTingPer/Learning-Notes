package Spark_RDD;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;

import java.util.ArrayList;
import java.util.Iterator;

public class Spark_RDD_flatmapDome
{
    public static void main(String[] args)
    {
        SparkConf conf = new SparkConf();
        conf.setMaster("local");
        conf.setAppName("Spark_RDD");

        JavaSparkContext sc = new JavaSparkContext(conf);
        sc.textFile("src/main/java/Spark_RDD/Spark.txt")
                .flatMap((FlatMapFunction<String, String>) s ->
                {
                    ArrayList<String> arr = new ArrayList<>();
                    for (String string : s.split(" "))
                    {
                        arr.add(string);
                    }
                    return arr.iterator();
                })
                .collect()
                .forEach(System.out::println);
        sc.close();
    }
}
