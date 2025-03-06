package Test05

import org.apache.spark.sql.functions._
import org.apache.spark.sql.{SaveMode, SparkSession}

import java.util.Properties

object 抽取06 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val  spark = SparkSession.builder()
            .enableHiveSupport().appName("hive")
            .config("hive.exec.dynamic.partition.mode","nonstrict")
            .master("local[*]")
            .getOrCreate()
        val mysqltable = "shtd_store.order_detail"
        val hivetable = "ods.order_detail"
        val tablename = "order_detail"
        val addfield = "create_time"
        Util.MySQlToHive(spark, mysqltable, hivetable, addfield)

        val odsmaxtime = spark.sql("select max(create_time) from ods.user_info").first()(0)
        print("--------------------")
        println(odsmaxtime)
        print("--------------------")
        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", tablename, conf)
            .where(col("create_time") > "2020-04-22 18:55:16")
            .withColumn("etl_date",lit("20250305"))
            .write
            .mode(SaveMode.Append)
            .format("hive")
            .partitionBy("etl_date")
            .saveAsTable(hivetable)
    }
}
