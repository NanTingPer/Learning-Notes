package _20250218

import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

import java.util.Properties

object 抽取01 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root");

        val spark = SparkSession
                .builder()
                .master("local[*]")
                .appName("dawf")
                .config("hive.exec.dynamic.partition.mode","nonstrict")
                .enableHiveSupport()
                .getOrCreate()

        val properties = new Properties()
        properties.put("user","root")
        properties.put("password","123456")

//        val maxtime = spark.sql("select max(modified_time) from ods.order_master").first().get(0)


        spark
                .read
                .jdbc("jdbc:mysql://192.168.45.13:3306/ds_db01?useSSL=false", "order_master", properties)
                .withColumn("etl_date",lit("20250218"))
                .write
                .format("hive")
                .mode(SaveMode.Overwrite)
                .partitionBy("etl_date")
                .saveAsTable("ods.order_master")
    }
}
