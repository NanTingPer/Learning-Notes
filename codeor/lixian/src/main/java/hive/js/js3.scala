package hive.js

object js3 {
    def main(args: Array[String]): Unit = {
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.types.DataTypes
        import org.apache.spark.sql.{SaveMode, SparkSession}

        import java.util.Properties
        val spark = SparkSession
            .builder()
            .config("hive.exec.dynamic.partition.mode","nonstrict")
            .master("local[*]")
            .appName("hive")
            .enableHiveSupport()
            .getOrCreate()

        val region = spark.table("dwd.dim_region").where(col("etl_date") === "20250407")
        val province = spark.table("dwd.dim_province").where(col("etl_date") === "20250407")
        val order_info = spark.table("dwd.fact_order_info")
            .where(year(col("create_time")) === 2020)
            .where(month(col("create_time")) === 4)

        val allavg = order_info.agg(avg("final_total_amount")).first()(0)

        //每个省
        val aggtable = order_info
            .groupBy("province_id")
            .agg(avg(col("final_total_amount")).cast(DataTypes.DoubleType) as "provinceavgconsumption")
            .withColumn("allprovinceavgconsumption", lit(allavg).cast(DataTypes.DoubleType))
        val conf = new Properties()
        conf.put("user", "root")
        conf.put("password", "123456")
        val myUdf = udf((d1 : Double, d2 : Double) => if(d1 > d2) "高" else if (d1 < d2) "低" else "相同")
        aggtable
            .withColumn("comparison", myUdf(col("provinceavgconsumption"),col("allprovinceavgconsumption")))
            .join(province, col("province_id") === province("id"))
            .select("province_id", "name", "provinceavgconsumption", "allprovinceavgconsumption", "comparison")
            .withColumnRenamed("province_id", "provinceid")
            .withColumnRenamed("name", "province_name")
            .write
            .mode(SaveMode.Overwrite)
            .jdbc("jdbc:mysql://192.168.45.13:3306/shtd_result?useSSL=false", "provinceavgcmp", conf)
    }
}
