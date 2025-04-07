package nicai

import org.apache.spark.sql.expressions.Window




object js4 {
  def main(args: Array[String]): Unit = {
    import org.apache.hudi.DataSourceWriteOptions._
    import org.apache.hudi.QuickstartUtils._
    import org.apache.hudi.config.HoodieWriteConfig._
    import org.apache.spark.sql.functions._
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
    import org.apache.spark.sql.types.DataTypes
    val order_info = spark.read.load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_info")
      .where(year(col("create_time")).cast(DataTypes.StringType) === "2020")
      .where(month(col("create_time")).cast(DataTypes.StringType) === "4")
    order_info.limit(10).show()
    val region = spark.read.load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_region")
      .where(col("etl_date") === "20250406")
    val province = spark.read.load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_province")
      .where(col("etl_date") === "20250406")
    val user_info = spark.read.load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_user_info")
      .where(col("etl_date") === "20250406")


    val province_avg = order_info
      .groupBy("province_id")
      .agg(avg(col("final_total_amount")) as "provinceavgconsumption")

    val myudf = udf((d1 : Double, d2 : Double) => if(d1 > d2) "高" else if(d1 < d2) "低" else "相同")
    var regin_avg = order_info
      .join(province, province("id") === col("province_id"))
      .join(region, region("id") === col("region_id"))
      .groupBy("region_id")
      .agg(avg(col("final_total_amount")) as "regionavgconsumption")
    regin_avg = regin_avg.join(province, Seq("region_id"))
      .withColumnRenamed("id", "province_id")
      .join(region,region("id")===col("region_id"))
//      .select(province("region_id"), col("regionavgconsumption"), col("province_id"))
    regin_avg.show()
    val conf = new Properties()
    conf.put("user","default")
    conf.put("password","123456")
    province_avg
      .join(regin_avg, regin_avg("province_id") === province_avg("province_id"))
      .select(province_avg("province_id"), col("provinceavgconsumption"), regin_avg("region_id"), regin_avg("region_name"), col("regionavgconsumption"), col("name"))
      .withColumn("comparison", myudf(col("provinceavgconsumption"),col("regionavgconsumption")))
      .select("province_id", "name", "provinceavgconsumption", "region_id", "region_name", "regionavgconsumption", "comparison")
      .withColumnRenamed("province_id", "provinceid")
      .withColumnRenamed("name", "provincename")
      .withColumnRenamed("region_id", "regionid")
      .withColumnRenamed("region_name", "regionname")
      .write
      .mode(SaveMode.Append)
      .jdbc("jdbc:clickhouse://192.168.45.13:8123/shtd_result","provinceavgcmpregion", conf)
//      .mode(SaveMode.Overwrite)
//      .format("hudi")
//      .options(getQuickstartWriteConfigs)
//      .option(SQL_ENABLE_BULK_INSERT.key(), "true")
//      .option(HIVE_STYLE_PARTITIONING.key(), "true")
//      .option(PARTITIONPATH_FIELD.key(), "year,month")
//      .option(RECORDKEY_FIELD.key(), "uuid")
//      .option(PRECOMBINE_FIELD.key(), "total_count")
//      .option(TBL_NAME.key(), "province_consumption_day_aggr")
//      .save("hdfs:///user/hive/warehouse/dws_ds_hudi.db/province_consumption_day_aggr")
  }
}
