package Spark_RDD;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.List;

public class Spark_RDD_Disk_File
{
    public static void main(String[] args)
    {
        SparkConf conf = new SparkConf();
        conf.setMaster("local");
        conf.setAppName("Spark_RDD_Disk");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> rdd = sc.textFile("src/main/java/Spark_RDD/Spark.txt");
        List<String> data = rdd.collect();
        data.forEach(System.out::println);
    }
}
