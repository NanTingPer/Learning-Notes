package _2025One1.js



object js4 {
    def main(args: Array[String]): Unit = {
        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.types.DataTypes

        import java.util.Properties
        import org.apache.spark.sql.{SaveMode, SparkSession}
        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .appName("hudi")
            .getOrCreate()

        val region = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_region").where(col("etl_date") === "20250409")
        val province = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_province").where(col("etl_date") === "20250409")
        val order_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_info")
        val order_detail = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_detail")
        val user_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_user_info").where(col("etl_date") === "20250409")

        val yx_order_info = order_info.where(year(col("create_time")) === 2020)

        //province zws
        val province_zws = yx_order_info
            .groupBy("province_id")
            .agg(expr("percentile(final_total_amount, 0.5)") as "provincemedian")

        val region_zws = yx_order_info
            .join(province, province("id") === col("province_id"))
            .join(region, region("id") === col("region_id"))
            .groupBy("region_id")
            .agg(expr("percentile(final_total_amount, 0.5)") as "regionmedian")


        val conf = new Properties()
        conf.put("user", "default")
        conf.put("password", "123456")
        province_zws
            .join(province, province("id") === col("province_id"))
            .select("province_id", "name", "provincemedian", "region_id")
            .join(region, region("id") === col("region_id"))
            .join(region_zws, Seq("region_id"))
            .select("province_id", "name", "region_id", "region_name", "provincemedian", "regionmedian")
            .withColumnRenamed("name", "provincename")
            .withColumnRenamed("province_id", "provinceid")
            .withColumnRenamed("region_id", "regionid")
            .withColumnRenamed("region_name", "regionname")
            .withColumn("provincemedian", col("provincemedian").cast(DataTypes.DoubleType))
            .withColumn("regionmedian", col("regionmedian").cast(DataTypes.DoubleType))

            .write
            .mode(SaveMode.Append)
            .jdbc("jdbc:clickhouse://192.168.45.13:8123/shtd_result", "nationmedian", conf)


//            .write
//            .mode(SaveMode.Append)
//            .jdbc("jdbc:clickhouse://192.168.45.13:8123/shtd_result", "topten", conf)

//            .write
//            .format("hudi")
//            .mode(SaveMode.Append)
//            .options(getQuickstartWriteConfigs)
//            .option(SQL_ENABLE_BULK_INSERT.key, "true")
//            .option(HIVE_STYLE_PARTITIONING.key, "true")
//            .option(RECORDKEY_FIELD.key, "uuid")
//            .option(PARTITIONPATH_FIELD.key, "year,month")
//            .option(PRECOMBINE_FIELD.key, "total_count")
//            .option(TBL_NAME.key, "province_consumption_day_aggr")
//            .save(s"hdfs:///user/hive/warehouse/dws_ds_hudi.db/province_consumption_day_aggr")

    }
}
