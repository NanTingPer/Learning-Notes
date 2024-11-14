package Spark_RDD;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;

import java.util.HashMap;
import java.util.Properties;

public class SparkSQLMySQL
{
    public static void main(String[] args)
    {
        SparkSession sparkSession = SparkSession
            .builder()
            .master("local[*]")
            .appName("SQlMysql")
            .getOrCreate();
        Properties MySQLc = new Properties();
        MySQLc.put("user","root");
        MySQLc.put("password","123456");
        Dataset<Row> 表 = sparkSession.read().jdbc("jdbc:xxxx", "表名", MySQLc);
        表.show();
        表.write().mode(SaveMode.Append).jdbc("jdbc:xxx","表名",MySQLc);//追加内容

        sparkSession.close();
    }

}
