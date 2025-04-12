package _2025Two.JS

object JS1 {
    def main(args: Array[String]): Unit = {
        import org.apache.hudi.DataSourceWriteOptions._
        import org.apache.hudi.QuickstartUtils._
        import org.apache.hudi.config.HoodieWriteConfig._
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.types.DataTypes
        import org.apache.spark.sql.{SaveMode, SparkSession}
        //--conf "spark.sql.extensions=org.apache.spark.sql.hudi.HoodieSparkSessionExtension" \
        //--conf "spark.serializer=org.apache.spark.serializer.KryoSerializer" \

        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .appName("hudi")
            .getOrCreate()

        val user_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_user_info").where(col("etl_date") === "20250410")
        val region = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_region").where(col("etl_date") === "20250410")
        val province = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_province").where(col("etl_date") === "20250410")
        val order_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_info")
        val order_detail = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_detail")

        order_info
            .withColumn("year", year(col("create_time")))
            .withColumn("month", month(col("create_time")))
            .groupBy("province_id", "year", "month")
            .agg(count("*") as "total_count", sum("final_total_amount") as "total_amount")
            .join(province, province("id") === col("province_id"))
            .join(region, region("id") === col("region_id"))
            .withColumn("sequence", row_number().over(Window.partitionBy("year", "month", "region_id").orderBy(col("total_amount").desc) ))
            .withColumn("uuid", expr("uuid()"))
            .select("uuid","province_id", "name", "region_id", "region_name", "total_amount", "total_count", "sequence", "year", "month")
            .withColumnRenamed("name", "province_name")
            .withColumn("total_count", col("total_count").cast(DataTypes.IntegerType))
            .write
            .format("hudi")
            .mode(SaveMode.Overwrite)
            .options(getQuickstartWriteConfigs)
            .option(SQL_ENABLE_BULK_INSERT.key,"true")
            .option(HIVE_STYLE_PARTITIONING.key, "true")
            .option(RECORDKEY_FIELD.key, "uuid")
            .option(PARTITIONPATH_FIELD.key, "year,month")
            .option(PRECOMBINE_FIELD.key, "total_count")
            .option(TBL_NAME.key, "province_consumption_day_aggr")
            .save(s"hdfs:///user/hive/warehouse/dws_ds_hudi.db/province_consumption_day_aggr")
    }
}
