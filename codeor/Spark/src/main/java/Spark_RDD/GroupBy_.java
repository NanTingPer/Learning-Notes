package Spark_RDD;

import com.google.protobuf.Internal;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import scala.Int;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class GroupBy_
{
    public static void main(String[] args)
    {
        SparkConf conf = new SparkConf();
        conf.setMaster("local");
        conf.setAppName("GroupBy");
        JavaSparkContext sc = new JavaSparkContext(conf);
        sc.textFile("src/main/java/Spark_RDD/Spark.txt")
            .flatMap((FlatMapFunction<String, String>) s ->
            {
                ArrayList<String> list = new ArrayList<>();
                for (String string : s.split(" "))
                {
                    list.add(string);
                }
                return list.iterator();
            })
            .groupBy((Function<String, String>) s -> s.substring(0, 1))
            .map((Function<Tuple2<String, Iterable<String>>, Tuple2<String, Integer>>) stringIterableTuple2 ->
            {
                int a =0;
                Iterator<String> iterator = stringIterableTuple2._2.iterator();
                while (iterator.hasNext())
                {
                    iterator.next();
                    a++;
                }
                return new Tuple2<>(stringIterableTuple2._1, a);
            }).collect()
            .forEach(System.out::println);
        sc.close();
    }
}
