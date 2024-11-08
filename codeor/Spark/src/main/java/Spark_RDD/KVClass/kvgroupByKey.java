package Spark_RDD.KVClass;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Iterator;

public class kvgroupByKey
{
    public static void main(String[] args)
    {
        SparkConf conf = new SparkConf();
        conf.setMaster("local");
        conf.setAppName("WordCount");
        //JavaSpark环境
        JavaSparkContext sc = new JavaSparkContext(conf);

        //创建元组并行数据源
        sc.parallelizePairs(Arrays.asList(
                new Tuple2<String, Integer>("a", 1),
                new Tuple2<String, Integer>("b", 2),
                new Tuple2<String, Integer>("b", 3),
                new Tuple2<String, Integer>("a", 4)
        )).groupByKey().collect().forEach(System.out::println);


        sc.close();

    }
}