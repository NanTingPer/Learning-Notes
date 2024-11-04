package Spark_RDD.KVClass;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class kvKnow_1
{
    public static void main(String[] args)
    {
        SparkConf conf = new SparkConf();
        conf.setMaster("local");
        conf.setAppName("KVClass");

        //构建Spark运行环境
        JavaSparkContext jsc = new JavaSparkContext(conf);

        //获取RDD
        jsc.parallelize(Arrays.asList(1,2,3,4,5,7))
                .mapToPair(num ->{
                    return new Tuple2<>(num,num * 2);
                })
                .mapValues(num -> num * 2)
                .collect()
                .forEach(System.out::println);
        //释放资源
        jsc.close();
    }
}
