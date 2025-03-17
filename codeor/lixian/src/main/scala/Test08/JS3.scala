package Test08

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.api.java.UDF2
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions._
import org.apache.spark.sql.types.DataTypes

import java.util.Properties
import scala.math.BigDecimal.javaBigDecimal2bigDecimal

object JS3 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession.builder()
            .master("local[*]")
            .appName("d")
            .config("hive.exec.dynamic.partition.mode","nonstrict")
            .enableHiveSupport()
            .getOrCreate()

        val dwstable = spark.table("dws.province_consumption_day_aggr")

        spark.udf.register("overDX", new UDF2[java.math.BigDecimal, java.math.BigDecimal ,String] {
            override def call(t1: java.math.BigDecimal, t2: java.math.BigDecimal): String = {
                if(t1 > t2) return "高"
                if(t1 < t2) return "低"
                "相同"
            }
        }, DataTypes.StringType)

        val win1 = Window.partitionBy("region_id")
        dwstable
            .where(col("year") === "2020")
            .where(col("month") === "4")
            .withColumn("provinceavgconsumption", (col("total_amount") / col("total_count")))
            .withColumn("sum1", sum(col("provinceavgconsumption")).over(win1))
            .withColumn("count1", count("*").over(win1))
            .withColumn("regionavgconsumption", (col("sum1") / col("count1")))
            .drop("sum1", "count1")
            .createTempView("table")
        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        spark.sql("select *, overDX(provinceavgconsumption, regionavgconsumption) as comparison from table")
            .select(col("province_id") as "provinceid", col("province_name") as "provincename", col("provinceavgconsumption"), col("region_id") as "regionid", col("region_name") as "regionname", col("regionavgconsumption"), col("comparison"))
            .write
            .jdbc("jdbc:mysql://192.168.45.13:3306/shtd_result?useSSL=false", "provinceavgcmpregion", conf)
    }
}
