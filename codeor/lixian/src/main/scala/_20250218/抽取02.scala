package _20250218

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions._

import java.util.Properties

object 抽取02 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")

        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        SparkSession
                .builder()
                .master("local[*]")
                .appName("dawf")
                .enableHiveSupport()
                .config("hive.exec.dynamic.partition.mode","nonstrict")
                .getOrCreate()
                .read
                .jdbc("jdbc:mysql://192.168.45.13:3306/ds_db01?useSSL=false","order_detail",conf)
                .withColumn("etl_date",lit("20250210"))
                .write
                .mode(SaveMode.Overwrite)
                .format("hive")
                .partitionBy("etl_date")
                .saveAsTable("ods.order_detail")
    }
}
