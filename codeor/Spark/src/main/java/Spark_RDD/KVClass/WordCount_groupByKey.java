package Spark_RDD.KVClass;

import org.apache.hadoop.yarn.webapp.hamlet2.Hamlet;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Iterator;

public class WordCount_groupByKey
{
    public static void main(String[] args)
    {
        SparkConf conf = new SparkConf();
        conf.setMaster("local[*]");
        conf.setAppName("WordCount_groupByKey");

        JavaSparkContext sc = new JavaSparkContext(conf);

        //创建元组并行数据源
        sc.parallelizePairs(Arrays.asList(
                new Tuple2<String, Integer>("a", 1),
                new Tuple2<String, Integer>("b", 2),
                new Tuple2<String, Integer>("b", 3),
                new Tuple2<String, Integer>("a", 4)
        )).reduceByKey((num1,num2) -> num1 + num2).collect()
          .forEach(System.out::println);

        sc.close();
    }
}
