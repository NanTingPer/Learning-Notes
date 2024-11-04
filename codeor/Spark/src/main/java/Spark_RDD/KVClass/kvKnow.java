package Spark_RDD.KVClass;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.*;
import java.io.*;

public class kvKnow
{
    public static void main(String[] args)
    {
        SparkConf conf = new SparkConf();
        conf.setMaster("local");
        conf.setAppName("KVClass");
        //创建元组
        Tuple2<String,Integer> t1 = new Tuple2<>("a",1);
        Tuple2<String,Integer> t2 = new Tuple2<>("b",2);
        Tuple2<String,Integer> t3 = new Tuple2<>("c",3);
        //创建元组集合
        List<Tuple2<String,Integer>> listTuple = new ArrayList<>();
        listTuple.add(t1);
        listTuple.add(t2);
        listTuple.add(t3);

        //构建RDD
        JavaSparkContext jsc = new JavaSparkContext(conf);
        jsc.parallelizePairs(listTuple)
                //只对值做操作
                .mapValues(tul1 ->tul1 * 2)
                .collect()
                .forEach(tuple ->{
                    System.out.print(tuple._1());
                    System.out.println(tuple._2());
                });
        //释放资源
        jsc.close();
    }
}
