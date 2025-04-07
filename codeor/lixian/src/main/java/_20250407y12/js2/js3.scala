package _20250407y12.js2

import org.apache.spark.sql.expressions.Window

object js3 {
  def main(args: Array[String]): Unit = {
    import org.apache.hudi.DataSourceWriteOptions._
    import org.apache.hudi.QuickstartUtils._
    import org.apache.hudi.config.HoodieWriteConfig._
    import org.apache.spark.sql.functions._
    import org.apache.spark.sql.types.DataTypes
    import org.apache.spark.sql.{SaveMode, SparkSession}

    val spark = SparkSession
      .builder()
      .appName("hive")
      .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .enableHiveSupport()
      .master("local[*]")
      .getOrCreate()

    val order_info = spark.read.load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_info")
    val region = spark.read.load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_region")
      .where(col("etl_date") === "20250406")
    val province = spark.read.load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_province")
      .where(col("etl_date") === "20250406")
    val user_info = spark.read.load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_user_info")
      .where(col("etl_date") === "20250406")

    val aggtable = order_info
      .withColumn("year", year(col("create_time")))
      .withColumn("month", month(col("create_time")))
      .groupBy("province_id", "year", "month")
      .agg(count("*") as "total_count", sum("final_total_amount") as "total_amount")
    val win1 = Window.partitionBy("year", "month", "region_id").orderBy(col("total_amount").desc)
    aggtable
      .join(province, province("id") === col("province_id"))
      .join(region, region("id") === col("region_id"))
      .select("province_id", "name", "region_id", "region_name", "total_amount", "total_count", "year", "month")
      .withColumnRenamed("name", "province_name")
      .withColumn("sequence", row_number().over(win1))
      .withColumn("uuid", expr("uuid()"))
      .select("uuid", "province_id", "province_name", "region_id", "region_name", "total_amount", "total_count", "sequence", "year", "month")

      .write
      .mode(SaveMode.Overwrite)
      .format("hudi")
      .options(getQuickstartWriteConfigs)
      .option(SQL_ENABLE_BULK_INSERT.key(), "true")
      .option(HIVE_STYLE_PARTITIONING.key(), "true")
      .option(PARTITIONPATH_FIELD.key(), "year,month")
      .option(RECORDKEY_FIELD.key(), "uuid")
      .option(PRECOMBINE_FIELD.key(), "total_count")
      .option(TBL_NAME.key(), "province_consumption_day_aggr")
      .save("hdfs:///user/hive/warehouse/dws_ds_hudi.db/province_consumption_day_aggr")
  }
}
