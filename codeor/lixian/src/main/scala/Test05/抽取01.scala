package Test05

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions._

import java.util.Properties

object 抽取01 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val  spark = SparkSession.builder()
            .enableHiveSupport().appName("hive")
            .config("hive.exec.dynamic.partition.mode","nonstrict")
            .master("local[*]")
            .getOrCreate()
        val mysqltable = "shtd_store.user_info"
        val hivetable = "ods.user_info"
        val tablename = "user_info"
        val addfield = "operate_time"
//        Util.MySQlToHive(spark, mysqltable, hivetable, addfield)

//        val odsmaxtime = spark.sql("select greatest(max(operate_time),max(create_time)) from ods.user_info").first()(0)
        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", tablename, conf)
//            .where(greatest("operate_time","create_time") > odsmaxtime)
            .withColumn("etl_date",lit("20250305"))
            .write
            .mode(SaveMode.Overwrite)
            .format("hive")
            .partitionBy("etl_date")
            .saveAsTable(hivetable)
    }
}
