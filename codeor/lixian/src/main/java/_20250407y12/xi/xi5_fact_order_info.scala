package _20250407y12.xi

import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions._
import org.apache.spark.sql.types.DataTypes
import org.apache.spark.sql.{SaveMode, SparkSession}

object xi5_fact_order_info {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession
            .builder()
            .appName("hive")
            .config("hive.exec.dynamic.partition.mode","nonstrict")
            .enableHiveSupport()
            .master("local[*]")
            .getOrCreate()

        val cols = spark.table("dwd.fact_order_info").columns.map(col)


        val agg = spark.sql("select * from ods.order_info").where(col("etl_date") === "20250406")
          .withColumn("etl_date", date_format(col("create_time"), "yyyyMMdd"))
          .withColumn("operate_time", coalesce(col("operate_time"), col("create_time")))
          .withColumn("dwd_insert_user", lit("user1"))
          .withColumn("dwd_modify_user", lit("user1"))
          .withColumn("dwd_insert_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
          .withColumn("dwd_modify_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
          .withColumn("create_time", date_sub(col("create_time"), 0))
          .groupBy("user_id", "create_time")
          .agg(sum("final_total_amount") as "sum", count("*") as "count")
        //ods

        val win1 = Window.partitionBy("user_id").orderBy("create_time")
        val win2 = Window.partitionBy("user_id")
        agg.withColumn("temNum", row_number().over(win1))
          .withColumn("subDate", date_sub(col("create_time"), col("temNum")))
          .withColumn("xunDay", count("*").over(win2))
          .where(col("xunDay") === 2)
          .show


//            .select(cols:_*)
//            .write
//            .mode(SaveMode.Append)
//            .format("hive")
//            .partitionBy("etl_date")
//            .saveAsTable("dwd.fact_order_info")
    }
}