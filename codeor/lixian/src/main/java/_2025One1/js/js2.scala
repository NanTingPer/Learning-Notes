package _2025One1.js

object js2 {
    def main(args: Array[String]): Unit = {
        import org.apache.hudi.DataSourceWriteOptions._
        import org.apache.hudi.config.HoodieWriteConfig._
        import org.apache.hudi.QuickstartUtils._
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.types.DataTypes
        import org.apache.spark.sql.{SaveMode, SparkSession}
        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .appName("hudi")
            .getOrCreate()

        val rr = spark.read.format("hudi").load(s"hdfs:///user/hive/warehouse/dws_ds_hudi.db/province_consumption_day_aggr")
            .show(1)
        val region = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_region").where(col("etl_date") === "20250409")
        val province = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_province").where(col("etl_date") === "20250409")
        val order_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_info")
        val user_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_user_info").where(col("etl_date") === "20250409")

        order_info
            .withColumn("year", year(col("create_time")))
            .withColumn("month", month(col("create_time")))
            .groupBy("province_id", "year", "month")
            .agg(count("*") as "total_count", sum("final_total_amount") as "total_amount")
            .join(province, col("province_id") === province("id"))
            .join(region, col("region_id") === region("id"))
            .withColumn("uuid", expr("uuid()"))
            .withColumn("sequence", row_number().over(Window.partitionBy("year", "month", "region_id").orderBy(col("total_amount").desc)))
            .select("uuid", "province_id", "name", "region_id", "region_name", "total_amount", "total_count", "sequence", "year", "month")
            .withColumnRenamed("name", "province_name")
            .write
            .format("hudi")
            .mode(SaveMode.Overwrite)
            .options(getQuickstartWriteConfigs)
            .option(SQL_ENABLE_BULK_INSERT.key, "true")
            .option(HIVE_STYLE_PARTITIONING.key, "true")
            .option(RECORDKEY_FIELD.key, "uuid")
            .option(PARTITIONPATH_FIELD.key, "year,month")
            .option(PRECOMBINE_FIELD.key, "total_count")
            .option(TBL_NAME.key, "province_consumption_day_aggr")
            .save(s"hdfs:///user/hive/warehouse/dws_ds_hudi.db/province_consumption_day_aggr")

    }
}
