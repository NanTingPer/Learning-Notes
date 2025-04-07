package _20250407y12.ji

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions._
import org.apache.spark.sql.types.DataTypes

import java.util.Properties
object ji2hive {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession
            .builder()
            .appName("hive")
            .config("hive.exec.dynamic.partition.mode","nonstrict")
            .enableHiveSupport()
            .master("local[*]")
            .getOrCreate()

        val region = spark.table("dwd.dim_region").where(col("etl_date") === "20250406")
        val province = spark.table("dwd.dim_province").where(col("etl_date") === "20250406")
        val order_info = spark.table("dwd.fact_order_info")

        //每个省份月下单
        val aggtable = order_info
            .withColumn("year", year(col("create_time")))
            .withColumn("month", month(col("create_time")))
            .groupBy("province_id", "year", "month")
            .agg(count("*") as "totalorder", sum("final_total_amount") as "totalconsumption")

        val conf = new Properties()
        conf.put("password", "123456")
        conf.put("user", "root")
        aggtable.join(province, aggtable("province_id") === province("id"))
            .join(region, col("region_id") === region("id"))
            .select("province_id", "name", "region_id","region_name", "totalconsumption", "totalorder", "year", "month")
            .withColumnRenamed("province_id", "provinceid")
            .withColumnRenamed("name", "provincename")
            .withColumnRenamed("region_id", "regionid")
            .withColumnRenamed("region_name", "regionname")
            .withColumn("totalconsumption", col("totalconsumption").cast(DataTypes.DoubleType))
            .withColumn("totalorder", col("totalorder").cast(DataTypes.IntegerType))
            .write
            .mode(SaveMode.Overwrite)
            .jdbc("jdbc:mysql://192.168.45.13:3306/shtd_result?useSSL=false", "provinceeverymonth", conf)
    }


}
