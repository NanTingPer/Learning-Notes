package _20250407y12.xi

import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.DataTypes
import org.apache.spark.sql.{SaveMode, SparkSession}

object xi6_fact_order_detail {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession
            .builder()
            .appName("hive")
            .config("hive.exec.dynamic.partition.mode","nonstrict")
            .enableHiveSupport()
            .master("local[*]")
            .getOrCreate()

        val cols = spark.table("dwd.fact_order_detail").columns.map(col)

        //ods
        val odstable = spark.sql("select * from ods.order_detail").where(col("etl_date") === "20250406")
            .withColumn("etl_date", date_format(col("create_time"), "yyyyMMdd").cast(DataTypes.StringType))
            .withColumn("dwd_insert_user", lit("user1"))
            .withColumn("dwd_modify_user", lit("user1"))
            .withColumn("dwd_insert_time", date_format(current_timestamp(),"yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
            .withColumn("dwd_modify_time", date_format(current_timestamp(),"yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
            .select(cols:_*)
            .write
            .mode(SaveMode.Append)
            .format("hive")
            .partitionBy("etl_date")
            .saveAsTable("dwd.fact_order_detail")
    }
}