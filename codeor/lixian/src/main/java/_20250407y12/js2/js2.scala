package _20250407y12.js2

object js2 {
  def main(args: Array[String]): Unit = {
    import org.apache.hudi.DataSourceWriteOptions._
    import org.apache.hudi.QuickstartUtils._
    import org.apache.hudi.config.HoodieWriteConfig._
    import org.apache.spark.sql.functions._
    import org.apache.spark.sql.types.DataTypes
    import org.apache.spark.sql.{SaveMode, SparkSession}

    import java.util.Properties

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
      .withColumn("day", date_format(col("create_time"), "dd"))
      .groupBy("user_id", "year", "month", "day")
      .agg(count("*") as "total_count", sum("final_total_amount") as "total_amount")

    aggtable.show()

    val fin = aggtable
      .withColumn("uuid", expr("uuid()"))
      .join(user_info, col("user_id") === user_info("id"))
      .select("uuid", "user_id", "name", "total_amount", "total_count", "year", "month", "day")
      .withColumnRenamed("name", "user_name")
      .withColumn("total_count", col("total_count").cast(DataTypes.IntegerType))
      .write
      .mode(SaveMode.Overwrite)
      .format("hudi")
      .options(getQuickstartWriteConfigs)
      .option(SQL_ENABLE_BULK_INSERT.key(), "true")
      .option(HIVE_STYLE_PARTITIONING.key(), "true")
      .option(PARTITIONPATH_FIELD.key(), "year,month,day")
      .option(RECORDKEY_FIELD.key(), "uuid")
      .option(PRECOMBINE_FIELD.key(), "total_count")
      .option(TBL_NAME.key(), "user_consumption_day_aggr")
      .save("hdfs:///user/hive/warehouse/dws_ds_hudi.db/user_consumption_day_aggr")
  }
}
