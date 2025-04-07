package 样卷1.数据采集

import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import java.util.Properties

object ALL06 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .master("local[*]")
            .appName("hive")
            .config("hive.exec.dynamic.partition.mode","nonstrict")
            .getOrCreate()

        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        val order_detail = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "order_detail", conf)
        order_detail
            .withColumn("etl_date",lit("20250402"))
            .write
            .mode(SaveMode.Append)
            .format("hive")
            .partitionBy("etl_date")
            .saveAsTable("ods.order_detail")
    }
}
