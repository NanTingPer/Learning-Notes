package Test05

import org.apache.spark.sql.functions._
import org.apache.spark.sql.{SaveMode, SparkSession}

import java.util.Properties

object 抽取03 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val  spark = SparkSession.builder()
            .enableHiveSupport().appName("hive")
            .config("hive.exec.dynamic.partition.mode","nonstrict")
            .master("local[*]")
            .getOrCreate()
        val mysqltable = "shtd_store.base_province"
        val hivetable = "ods.base_province"
        val tablename = "base_province"
        val addfield = "id"
//        Util.MySQlToHive(spark, mysqltable, hivetable, addfield)

//        val odsmaxtime = spark.sql("select max(id) from ods.user_info").first()(0)
        println("----------------------------")
//        println(odsmaxtime)
        println("----------------------------")
        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", tablename, conf)
//            .where(col("id") > 32)
            .withColumn("create_time", date_format(current_timestamp(),"yyyy-MM-dd HH:mm:ss"))
            .withColumn("etl_date",lit("20250305"))
            .write
            .mode(SaveMode.Overwrite)
            .format("hive")
            .partitionBy("etl_date")
            .saveAsTable(hivetable)
//        id:bigint,name:string,region_id:string,area_code:string,iso_code:string,etl_date:string
//        id:bigint,name:string,region_id:string,area_code:string,iso_code:string,create_time:string,etl_date:string
    }
}
