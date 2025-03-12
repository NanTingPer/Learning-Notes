package Test07

import org.apache.spark.sql.functions._
import org.apache.spark.sql.{SaveMode, SparkSession}

import java.util.Properties

object E06 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession.builder()
            .appName("hive")
            .enableHiveSupport()
            .config("hive.exec.dynamic.partition.mode","nonstrict")
            .master("local[*]")
            .getOrCreate()
        val sqldataname = "shtd_store"
        val sqltablename = "order_detail"
        val hivedata = "ods.order_detail"

        val sql = sqldataname + "." + sqltablename
        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        spark.read.jdbc(s"jdbc:mysql://192.168.45.13:3306/${sqldataname}?useSSL=false", sqltablename, conf)
            .withColumn("etl_date", lit("20250310"))
            .write
            .mode(SaveMode.Overwrite)
            .format("hive")
            .partitionBy("etl_date")
            .saveAsTable(hivedata)

    }
}
