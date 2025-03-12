package Test07

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.api.java.UDF2
import org.apache.spark.sql.catalyst.dsl.expressions.StringToAttributeConversionHelper
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions._
import org.apache.spark.sql.types.DataTypes

import java.util.Properties

object L03 {
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

        val yesData = order_info.withColumn("tempdate", date_format(col("create_time"), "yyyyMM"))
            .where(col("tempdate") === lit("202004"))

        val aggggg = yesData
            .groupBy("province_id")
            .agg(sum("final_total_amount") / count("*") as "provinceavgconsumption")

        spark.udf.register("dawd", new UDF2[Double, Double, String] {
            override def call(t1: Double, t2: Double): String = {
                if(t1 > t2)
                    return "高"
                if(t1 < t2)
                    return "低"
                "相同"
            }
        }, DataTypes.StringType)

        var avg = aggggg.agg(sum("provinceavgconsumption")/count("*"))
        aggggg.withColumn("allprovinceavgconsumption", lit(avg.first()(0)).cast(DataTypes.DoubleType))
            .withColumn("provinceavgconsumption", col("provinceavgconsumption").cast(DataTypes.DoubleType))
//            .show()
            .createTempView("fin")


        spark.sql("select *,dawd(provinceavgconsumption, allprovinceavgconsumption) as comparison from fin")
        .join(province, province("id") === col("province_id"))
        .select(col("province_id").as("provinceid"),
            col("name").as("provincename"),
            col("provinceavgconsumption").as("provinceavgconsumption"),
            col("allprovinceavgconsumption").as("allprovinceavgconsumption"),
            col("comparison").as("comparison"))
            .write
            .jdbc("jdbc:mysql://192.168.45.13:3306/shtd_result?useSSL=false", "provinceavgcmp", conf)
    }

}
