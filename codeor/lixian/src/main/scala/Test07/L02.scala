package Test07

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

import java.util.Properties

object L02 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession.builder()
            .appName("hive")
            .enableHiveSupport()
            .config("hive.exec.dynamic.partition.mode","nonstrict")
            .master("local[*]")
            .getOrCreate()

        val province = spark.sql("select * from dwd.dim_base_province")
        val order_info = spark.sql("select * from dwd.fact_order_info")
        val region = spark.sql("select * from dwd.dim_region")

        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        val info_select = order_info.select("id", "final_total_amount", "user_id", "create_time", "province_id")
        info_select
            .withColumn("month", date_format(col("create_time"), "MM"))
            .withColumn("year", date_format(col("create_time"), "yyyy"))
            .groupBy("province_id", "month", "year")
            .agg(count("id") as "totalorder", sum("final_total_amount") as "totalconsumption")
            .join(province, col("province_id") === province("id"))
            .select("province_id", "month", "year", "totalorder", "totalconsumption", "name", "region_id")
            .withColumnRenamed("name", "provincename")
            .join(region, col("region_id") === region("id"))
//            .select("province_id", "month", "year", "totalorder", "totalconsumption", "name", "region_id", "region_name")
            .select("province_id", "provincename", "region_id", "region_name", "totalconsumption", "totalorder", "year","month")
            .write
            .jdbc("jdbc:mysql://192.168.45.13:3306/shtd_result?useSSL=false", "provinceeverymonth", conf)
    }

}
