package SparkSQL;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;
import org.sparkproject.jetty.server.session.Session;

public class SparkHive
{
    public static void main(String[] args)
    {
        var spark = SparkSession.
        //设置访问hive的用户名
        System.setProperty("HADOOP_USER_NAME","root");
        SparkSession sparkSession = SparkSession
            .builder()
            //启用Hive支持
            .enableHiveSupport()
            .master("local[*]")
            .appName("SpakrHive")
            .getOrCreate();

        sparkSession.sql("show tables").show();

        sparkSession.close();
    }
}
