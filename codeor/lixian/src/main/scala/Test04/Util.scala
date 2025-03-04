package Test04

import org.apache.spark.sql
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions._

import java.util.Properties

object Util {
    def GetSpark ={
        SparkSession
            .builder()
            .enableHiveSupport()
            .master("local[*]")
            .appName("app")
            .config("hive.exec.dynamic.partition.mode","nonstrict")
//            .config("spark.sql.Ov")
            .getOrCreate()
    }

    def GetMySQLData(sparkSession: SparkSession, database: String, tablename: String): sql.DataFrame = {
        val conf = new Properties()
        conf.put("user", "root")
        conf.put("password", "123456")
        sparkSession.read.jdbc(s"jdbc:mysql://192.168.45.13:3306/${database}?useSSL=false", tablename, conf)
    }

    /**
     * 给定一个myql的数据表，给定一个hive的数据表，将mysql数据对半注入hive
     * @param sparkSession spark
     * @param mysqlName mysql数据库
     * @param hiveName hive数据库
     * @return
     */
    def MySQlToHive(sparkSession: SparkSession, mysqlName : String, hiveName : String, 表内字段 : String) ={
        //传入ods.order_info 分解 => 库: ods  表: order_info
        val mysql = mysqlName.split('.')
        println(mysql.length)
        val mysqldatabase = mysql(0)
        val mysqltable = mysql(1)
        val hive = hiveName.split('.')
        val hivedatabes = hive(0)
        val hivetable = hive(1)

        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        sparkSession.read.jdbc(s"jdbc:mysql://192.168.45.13:3306/${mysqldatabase}?useSSL=false", mysqltable, conf).createTempView("mysql")
        val CentCount = sparkSession.sql("select count(*) from mysql").first()(0)
        sparkSession.sql("select * from mysql").orderBy(col(s"${表内字段}")).limit(CentCount.toString.toInt / 2)
            .withColumn("etl_date", lit("20250303"))
//            .withColumn("create_time", date_format(current_timestamp(), "yyyy-MM-dd HH-mm-ss")) //比赛时根据原始行进行格式化
            .write
            .partitionBy("etl_date")
            .mode(SaveMode.Overwrite)
            .format("hive")
            .saveAsTable(hiveName);
    }

}
