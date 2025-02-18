package _20250218

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions.lit

import java.util.Properties

object 抽取03 {
    def main(args: Array[String]): Unit = {
        val c = new Properties()
        c.put("user","root")
        c.put("password","123456")
        System.setProperty("HADOOP_USER_NAME","root")
        SparkSession
                .builder()
                .master("local[*]")
                .enableHiveSupport()
                .config("hive.exec.dynamic.partition.mode","nonstrict")
                .getOrCreate()
                .read
                .jdbc("jdbc:mysql://192.168.45.13:3306/ds_db01?useSSL=false","coupon_info", c)
                .withColumn("etl_date",lit("20250218"))
                .write
                .format("hive")
                .partitionBy("etl_date")
                .mode(SaveMode.Overwrite)
                .saveAsTable("ods.coupon_info")
    }
}
