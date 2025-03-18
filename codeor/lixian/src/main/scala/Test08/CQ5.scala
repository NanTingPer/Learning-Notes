package Test08

import org.apache.spark.sql.functions._
import org.apache.spark.sql.{SaveMode, SparkSession}

import java.util.Properties

object CQ5 {
    def getSpark : SparkSession = {
        val spark = SparkSession.builder()
            .master("local[*]")
            .appName("d")
            .config("hive.exec.dynamic.partition.mode","nonstrict")
            .enableHiveSupport()
            .getOrCreate()
        spark
    }

    def getsqlConf() : Properties = {
        val conf = new Properties()
        conf.put("user", "root")
        conf.put("password", "123456")
        conf
    }
    def ETLOneData(spark : SparkSession, hiveTable : String, SQLData : String, SQLTable : String) : Unit = {
        val oneData = spark.read.jdbc(s"jdbc:mysql://192.168.45.13:3306/${SQLData}?useSSL=false", SQLTable, getsqlConf()).limit(1)

        oneData.withColumn("etl_date", lit("20250315"))
            .write
            .format("hive")
            .mode(SaveMode.Overwrite)
            .partitionBy("etl_date")
            .saveAsTable(hiveTable)
    }
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME", "root")
        val hivetable = "ods.order_info"
        val sqltable = "order_info"
        val sqldata = "shtd_store"
        val spark = getSpark
        ETLOneData(spark, hivetable, sqldata, sqltable)

//        val hivedata = spark.table(hivetable)
        val mysqldata = spark.read.jdbc(s"jdbc:mysql://192.168.45.13:3306/${sqldata}?useSSL=false", sqltable, getsqlConf())

//        val maxtime = hivedata.select(greatest(max("operate_time"), max("create_time"))).first()(0)

        mysqldata/*.where(greatest("operate_time", "create_time") > maxtime)*/
            .withColumn("etl_date", lit("20250316"))
            .write
            .format("hive")
            .mode(SaveMode.Overwrite)
            .partitionBy("etl_date")
            .saveAsTable(hivetable)

    }
}
