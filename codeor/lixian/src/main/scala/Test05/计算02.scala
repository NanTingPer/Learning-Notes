package Test05

import org.apache.hadoop.hive.ql.exec.UDF
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.api.java.UDF2
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions._
import org.apache.spark.sql.types.DataTypes

import java.util.Properties
object 计算02 {
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
        val simpleProvince = province.select("id", "name")

        val table = order_info
            .groupBy("province_id")
            .agg(sum("final_total_amount") as "provinceavgconsumption", count("*") as "count")
            .withColumn("provinceavgconsumption", col("provinceavgconsumption") / col("count"))
            .select(col("provinceavgconsumption"), col("province_id") as "provinceid")
            .join(province, col("provinceid") === province("id"))
            .select(col("provinceid"), col("name") as "provincename", col("provinceavgconsumption"))
            .groupBy("provinceid")
            .agg(sum("provinceavgconsumption").cast(DataTypes.DoubleType) as "provinceavgconsumption")
        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        spark.udf.register("compar", new UDF2[Double, Double, String] {
            override def call(t1: Double, t2: Double): String = {
                val t3 = t1.toDouble
                val t4 = t2.toDouble
                if(t3 > t4) "高"
                else if (t3 < t4) "低"
                else "相同"
            }
        }, DataTypes.StringType)
        val allption = table.agg(sum("provinceavgconsumption")).first()(0)
        val r = table.count();
         val allprovinceavgconsumption =  allption.toString.toFloat / r
        table.withColumn("allprovinceavgconsumption", lit(allprovinceavgconsumption).cast(DataTypes.DoubleType))
        .createTempView("table")
        spark.sql("select *,compar(provinceavgconsumption,allprovinceavgconsumption)  from table")
            .withColumnRenamed("compar(provinceavgconsumption, allprovinceavgconsumption)", "comparison")
            .join(simpleProvince, col("provinceid") === simpleProvince("id"))
            .drop(simpleProvince("id"))
            .select(col("provinceid"), col("name") as "provincename", col("provinceavgconsumption"), col("allprovinceavgconsumption"), col("comparison"))
            .write
            .jdbc("jdbc:mysql://192.168.45.13/shtd_result?useSSL=false", "provinceavgcmp",conf)
    }

}
