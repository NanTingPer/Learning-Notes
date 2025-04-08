package hive.js


object js2 {
    def main(args: Array[String]): Unit = {
        import org.apache.spark.sql.functions._
        import java.util.Properties
        import org.apache.spark.sql.types.DataTypes
        import org.apache.spark.sql.{SaveMode, SparkSession}
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

        val aggtable = order_info
            .withColumn("year", year(col("create_time")))
            .withColumn("month", month(col("create_time")))
            .groupBy("province_id", "year", "month")
            .agg(count("*") as "totalorder", sum("final_total_amount") as "totalconsumption")

        val fin = aggtable
            .join(province, province("id") === aggtable("province_id"))
            .join(region, region("id") === col("region_id"))
            .select("province_id", "name", "region_id", "region_name", "totalconsumption", "totalorder", "year", "month")
            .withColumnRenamed("province_id", "provinceid")
            .withColumnRenamed("name", "provincename")
            .withColumnRenamed("region_id", "regionid")
            .withColumnRenamed("region_name", "regionname")
            .withColumn("totalconsumption", col("totalconsumption").cast(DataTypes.DoubleType))
            .withColumn("totalorder", col("totalorder").cast(DataTypes.IntegerType))
        fin.show

        val conf = new Properties()
        conf.put("user", "root")
        conf.put("password", "123456")
        fin
            .write
            .mode(SaveMode.Overwrite)
            .jdbc("jdbc:mysql://192.168.45.13:3306/shtd_result?useSSL=false", "provinceeverymonth", conf)
    }
}
