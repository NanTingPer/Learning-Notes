package _20250407y12.xi

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions._
import org.apache.spark.sql.types.DataTypes

object xi1_dim_user_info {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession
            .builder()
            .appName("hive")
            .config("hive.exec.dynamic.partition.mode","nonstrict")
            .enableHiveSupport()
            .master("local[*]")
            .getOrCreate()

        //ods
        val odstable = spark.sql("select * from ods.user_info").where(col("etl_date") === "20250406")

        //dwd
        val dwdtable = spark.sql("select * from dwd.dim_user_info")
        val maxpar = dwdtable.select(max(col("etl_date"))).first()(0)
        val newdwdtable = dwdtable.where(col("etl_date") === maxpar)
            .withColumn("operate_time", coalesce(col("operate_time"),col("create_time")))
        val cols = newdwdtable.columns.map(col)

        //ods插
        val unionTable = odstable
            .withColumn("operate_time", coalesce(col("operate_time"), col("create_time")))
            .withColumn("dwd_insert_user", lit("user1"))
            .withColumn("dwd_insert_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
            .withColumn("dwd_modify_user", lit("user1"))
            .withColumn("dwd_modify_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
            .select(cols: _*)
            .union(newdwdtable)

        //合并修改
        val win1 = Window.partitionBy("id").orderBy(col("operate_time").desc)
        val win2 = Window.partitionBy("id")

        unionTable
            .withColumn("temp", row_number().over(win1))
            .withColumn("dwd_modify_time", max(col("dwd_modify_time")).over(win2))
            .withColumn("dwd_insert_time", min(col("dwd_insert_time")).over(win2))
            .where(col("temp") === 1)
            .drop("temp")
            .withColumn("etl_date", lit("20250406"))
            .write
            .mode(SaveMode.Append)
            .format("hive")
            .partitionBy("etl_date")
            .saveAsTable("dwd.dim_user_info")
    }
}
