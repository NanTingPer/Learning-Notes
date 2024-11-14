package Spark_RDD.KVClass;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class WordCount
{
    public static void main(String[] args)
    {
        SparkConf conf = new SparkConf();
        conf.setMaster("local");
        conf.setAppName("WordCount");
        JavaSparkContext sc = new JavaSparkContext(conf);
        //获取文件源
        sc.textFile("src/main/java/Spark_RDD/KVClass/Word.txt")
          //切分
                .flatMap(strs -> Arrays.asList(strs.split(" ")).iterator())
                .groupBy(words -> words)
                .mapValues(strnum ->{
                    int count = 0;
                    Iterator<String> iterator = strnum.iterator();
                    while (iterator.hasNext())
                    {
                        iterator.next();
                        count ++;
                    }
                    return count;
                })
                  .collect()
                  .forEach(System.out::println);

        sc.close();

    }
}
