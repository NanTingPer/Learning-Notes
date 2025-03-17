package Test08

import org.apache.spark.sql.expressions._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{SaveMode, SparkSession}

import java.util.Properties

object QX5 {
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

    def dataToHive(spark : SparkSession, odsHive : String, dwdHive : String) ={
        spark.table(odsHive).where(col("etl_date") === "20250315")
            .withColumn("dwd_insert_user", lit("user1"))
            .withColumn("dwd_insert_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss"))
            .withColumn("dwd_modify_user", lit("user1"))
            .withColumn("dwd_modify_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss"))
            .write
            .mode(SaveMode.Overwrite)
            .format("hive")
            .partitionBy("etl_date")
            .saveAsTable(dwdHive)

    }

    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME", "root")
        val odstable = "ods.order_info"
        val dwdtable = "dwd.fact_order_info"
        val spark = getSpark

        dataToHive(spark, odstable, dwdtable)


        val odsdata = spark.table(odstable).where(col("etl_date") === "20250316")
        val dwddata = spark.table(dwdtable)
        val cols = dwddata.columns.map(col)


        odsdata
            .withColumn("temp_operate_time", coalesce(col("operate_time"), col("create_time")))
            .drop("operate_time")
            .withColumnRenamed("temp_operate_time", "operate_time")
            .withColumn("etl_date", date_format(col("create_time"), "yyyyMMdd"))
            .withColumn("dwd_insert_user", lit("user1"))
            .withColumn("dwd_insert_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss"))
            .withColumn("dwd_modify_user", lit("user1"))
            .withColumn("dwd_modify_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss"))
            .select(cols:_*)


            .write
            .mode(SaveMode.Append)
            .format("hive")
            .partitionBy("etl_date")
            .saveAsTable(dwdtable)
    }

}
