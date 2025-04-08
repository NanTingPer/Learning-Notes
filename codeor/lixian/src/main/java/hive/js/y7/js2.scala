package hive.js.y7

object js2 {
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
        val win1 = Window.partitionBy("year", "month", "region_id").orderBy(col("total_amount").desc)

        val fin = order_info
            .withColumn("year", year(col("create_time")))
            .withColumn("month", month(col("create_time")))
            .groupBy("province_id", "year", "month")
            .agg(count("*") as "total_count", sum("final_total_amount") as "total_amount")
            .join(province, province("id") === col("province_id"))
            .join(region, region("id") === col("region_id"))
            .withColumn("sequence", row_number().over(win1))
            .select("province_id", "name", "region_id", "region_name", "total_amount", "total_count", "sequence", "year", "month")
            .withColumnRenamed("name", "province_name")
//            .withColumn("total_amount", col("total_amount").cast(DataTypes.DoubleType))
        fin.show
        fin
            .write
            .mode(SaveMode.Overwrite)
            .format("hive")
            .saveAsTable("dws.province_consumption_day_aggr")
    }
}
