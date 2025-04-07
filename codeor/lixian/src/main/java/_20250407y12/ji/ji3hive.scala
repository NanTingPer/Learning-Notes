package _20250407y12.ji

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.DataTypes
import org.apache.spark.sql.expressions._
import java.util.Properties

object ji3hive {
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

        val yxorder_info = order_info
            .where(year(col("create_time")) === 2020)
            .where(month(col("create_time")) === 4)

        val myudf = udf((d1 : Double, d2 : Double) => if(d1 > d2) "高" else if (d1 < d2) "低" else "相同")
        val 所有 = yxorder_info.agg(avg(col("final_total_amount"))).first()(0)


        val conf = new Properties()
        conf.put("password", "123456")
        conf.put("user", "root")
        yxorder_info
            .groupBy("province_id").agg(avg(col("final_total_amount")) as "provinceavgconsumption")
            .withColumn("allprovinceavgconsumption", lit(所有))
            .join(province, col("province_id") === province("id"))
            .select("province_id", "name", "provinceavgconsumption", "allprovinceavgconsumption")
            .withColumn("comparison", myudf(col("provinceavgconsumption"), col("allprovinceavgconsumption")))
            .withColumn("provinceavgconsumption", col("provinceavgconsumption").cast(DataTypes.DoubleType))
            .withColumn("allprovinceavgconsumption", col("allprovinceavgconsumption").cast(DataTypes.DoubleType))
            .withColumnRenamed("province_id", "provinceid")
            .withColumnRenamed("name", "provincename")
            .write
            .mode(SaveMode.Overwrite)
            .jdbc("jdbc:mysql://192.168.45.13:3306/shtd_result?useSSL=false", "provinceavgcmp", conf)
    }


}
