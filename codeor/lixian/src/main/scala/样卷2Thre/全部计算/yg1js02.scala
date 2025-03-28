package 样卷2Thre.全部计算

import org.apache.spark.sql.SaveMode



object yg1js02 {
    def main(args: Array[String]): Unit = {
        import org.apache.spark.sql.SparkSession
        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.functions._

        import java.util.Properties
        val spark = SparkSession.builder()
            .enableHiveSupport()
            .appName("wfawfawf")
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
            .getOrCreate()

        val url = "jdbc:mysql://192.168.45.13:3306/shtd_result?useSSL=false"
        val order_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_info")
        val province = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_province").where(col("etl_date") === "20250326")
        val region = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_region").where(col("etl_date") === "20250326")

        val agg = order_info.select("province_id", "final_total_amount", "create_time")
            .withColumn("year", year(col("create_time")))
            .withColumn("month", month(col("create_time")))
            .groupBy("province_id", "year", "month")
            .agg(count("*") as "totalorder", sum("final_total_amount") as "totalconsumption")

        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        agg.join(province, col("province_id") === province("id"))
            .join(region, col("region_id") === region("id"))
            .select("province_id", "name", "region_id", "region_name","totalconsumption","totalorder","year","month")
            .withColumnRenamed("province_id","provinceid")
            .withColumnRenamed("name","provincename")
            .withColumnRenamed("region_id","regionid")
            .withColumnRenamed("region_name","regionname")
            .write
            .jdbc(url, "provinceeverymonth", conf)

    }
}
