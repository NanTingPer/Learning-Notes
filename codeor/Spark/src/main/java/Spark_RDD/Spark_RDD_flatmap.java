package Spark_RDD;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Spark_RDD_flatmap
{
    public static void main(String[] args)
    {
        SparkConf conf =new SparkConf();
        conf.setAppName("Spark_RDD");
        conf.setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);

        sc.parallelize(Arrays.asList(Arrays.asList(1, 2, 3, 4), Arrays.asList(5, 6, 7, 8)))
                .flatMap((FlatMapFunction<List<Integer>, Integer>) integers ->
                {
                    List<Integer> list = new ArrayList<>();
                    integers.forEach(unm -> list.add(unm + 2));
                    return list.iterator();
                })
                .collect()
                .forEach(System.out::println);

        sc.close();

    }
}
