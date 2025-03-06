package Test05

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, concat, count, date_format, sum}
import org.apache.spark.sql.expressions._

import java.util.Properties

object 计算01 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val  spark = SparkSession.builder()
            .enableHiveSupport().appName("hive")
            .config("hive.exec.dynamic.partition.mode","nonstrict")
            .master("local[*]")
            .getOrCreate()

        val province = spark.sql("select * from dwd.base_province")
        val region = spark.sql("select * from dwd.dim_region")
        val order_info = spark.sql("select * from dwd.fact_order_info")
        val order_detail = spark.sql("select * from dwd.fact_order_detail")

        var provinceJoin = province.join(region, col("region_id") === region("id"))
        provinceJoin = provinceJoin.select(province("id").as("provinceid"), provinceJoin("name").as("provincename"), provinceJoin("region_id").as("region_id"), provinceJoin("region_name").as("regionname")).distinct()

        val win1 = Window.partitionBy("")
        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        order_info
            .withColumn("month", date_format(col("create_time"), "MM"))
            .withColumn("year", date_format(col("create_time"),"yyyy"))
            .groupBy("user_id","year","month","province_id")
            .agg(count("user_id") as "totalorder", sum("final_total_amount") as "totalconsumption")
            .join(provinceJoin, col("province_id") === provinceJoin("provinceid"))
            .groupBy("provinceid", "provincename", "region_id","regionname","year","month")
            .agg(sum("totalconsumption") as "totalconsumption" , sum("totalorder") as "totalorder")
            .write
            .jdbc("jdbc:mysql://192.168.45.13/shtd_result?useSSL=false", "provinceeverymonth",conf)



    }
}
