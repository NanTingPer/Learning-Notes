package Test01;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.mapreduce.*;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.SparkSession;

public class SparkOnHBase
{
    public static void main(String[] args)
    {
        System.setProperty("HADOOP_USER_NAME","root");

        SparkConf sconf = new SparkConf();
        sconf.setAppName("awf");
        sconf.setMaster("local[*]");
        SparkSession spark = new SparkSession(new SparkContext(sconf));


        Configuration conf = new Configuration();
//        conf.set(TableInputFormat);
//        spark.sparkContext().newAPIHadoopRDD(conf)
    }
}
