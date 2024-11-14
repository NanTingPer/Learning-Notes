package Spark_RDD;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.VoidFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Spark_RDD_Memory
{
    public static void main(String[] args)
    {
        SparkConf conf = new SparkConf();
        conf.setMaster("local");
        conf.setAppName("Spark_01");
        JavaSparkContext sc = new JavaSparkContext(conf);
        List<String> list = new ArrayList<String>();
        list.add("zhangsan");
        list.add("lisi");
        list.add("wangwu");
        //利用Spark环境 对接内存数据源 并构建RDD
        JavaRDD<String> javaRDD = sc.parallelize(list);
        //收集数据
        List<String> cool = javaRDD.collect();
        cool.forEach(a -> System.out.println(a));
        sc.close();

    }
}
