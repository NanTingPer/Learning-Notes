package _20250407y12.chou

import org.apache.spark.sql.functions._
import org.apache.spark.sql.{SaveMode, SparkSession}

import java.util.Properties

object chou6_order_detail {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession
            .builder()
            .appName("hive")
            .config("hive.exec.dynamic.partition.mode","nonstrict")
            .enableHiveSupport()
            .master("local[*]")
            .getOrCreate()

        //抽取mysql
        val conf = new Properties()
        conf.put("password", "123456")
        conf.put("user", "root")
        val mysql = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "order_detail", conf)

        //ods最大
        val odstable = spark.sql("select * from ods.order_detail")
        val odsmaxtime = odstable.select(max("create_time")).first()(0)
        val cols = odstable.columns.map(col)

        //mysql增量
        mysql
            .where(col("create_time") > odsmaxtime)
            .withColumn("etl_date", lit("20250406"))
            .select(cols:_*)
            .write
            .mode(SaveMode.Append)
            .format("hive")
            .partitionBy("etl_date")
            .saveAsTable("ods.order_detail")

    }
}
