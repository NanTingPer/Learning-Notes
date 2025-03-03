package Test04

import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.functions._

object 抽取03 {
    def main(args: Array[String]): Unit = {
        //    抽取shtd_store库中base_province的增量数据进入Hive的 ods库中表base_province。
        //    根据ods.base_province表中id作为增量字段，只将新增的数据抽入，
        //    字段名称、类型不变并添加字 段 create_time 取当前时间，同时添加静态分区，分区字段为etl_date，类型为String，
        //          且值为当前比赛日的前一天日期（分区字段格式为 yyyyMMdd）。
        //    使用hive cli执行show partitions ods.base_province 命令，将结果截图粘贴至客户端桌面【Release\任务B提交结果.docx】中对应的任务序号下；    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME", "root")
        /**
         * spark实例
         */
        val spark = Util.GetSpark
        /**
         * hive库与表
         */
        val hive = "ods.province"
        /**
         * mysql库与表
         */
        val mysql = "shtd_store.base_province"
        /**
         * mysql表
         */
        val mysqlTable = "base_province"
        /**
         * mysql数据库
         */
        val mysqlData = "shtd_store"
        /**
         * 增量字段
         */
        val addField = "create_time"

        Util.MySQlToHive(spark, mysql, hive, addField)
        val sqlqdata = Util.GetMySQLData(spark, mysqlData, mysqlTable).createTempView("mysqld")

        val hivemaxtime = spark.sql(s"select  max(${addField}) as maxtime from ${hive}") /*.select(date_sub(col("maxtime"), days =  1))*/.first()(0)
        println("hive最大时间: " + hivemaxtime) //hive最大时间: 2021-01-01 12:21:13.0
        val sqlmaxtime = spark.sql(s"select max(${addField}) from mysqld").first()(0)
        println("mys最大时间: " + sqlmaxtime) //hive最大时间: 2021-01-01 12:21:13.0


        spark.sql(s"select * from mysqld where create_time > '${hivemaxtime}'")
            .withColumn("etl_date", lit("20250304"))
            .write
            .partitionBy("etl_date")
            .mode(SaveMode.Append)
            .format("hive")
            .saveAsTable(hive)

    }
}

