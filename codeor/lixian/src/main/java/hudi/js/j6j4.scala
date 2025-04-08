package hudi.js



// --conf "spark.sql.extensions=org.apache.spark.sql.hudi.HoodieSparkSessionExtension" \
// --conf "spark.serializer=org.apache.spark.serializer.KryoSerializer"
object j6j4 {
    def main(args: Array[String]): Unit = {
        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.{SaveMode, SparkSession}

        import java.util.Properties
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .master("local[*]")
            .getOrCreate()
        import org.apache.spark.sql.types.DataTypes
        val region = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_region").where(col("etl_date") === "20250407")
        val province = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_province").where(col("etl_date") === "20250407")
        val user_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_user_info").where(col("etl_date") === "20250407")
        val order_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_info")
        val order_detail = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_detail")

        val order_data = order_info
            .where(year(col("create_time")) === 2020)

        //地区中位数
        val region_zws = order_data
            .join(province, province("id") === col("province_id"))
            .join(region, region("id") === col("region_id"))
            .groupBy("region_id")
            .agg(expr("percentile(final_total_amount, 0.5)") as "regionmedian")
            .select("region_id", "regionmedian")

        val province_zws = order_data
            .groupBy("province_id")
            .agg(expr("percentile(final_total_amount, 0.5)") as "provincemedian")
            .join(province, province("id") === col("province_id"))
            .join(region, region("id") === col("region_id"))
            .join(region_zws, Seq("region_id"))

        val conf = new Properties()
        conf.put("user","default")
        conf.put("password","123456")

        province_zws
            .select("province_id", "name", "region_id", "region_name", "provincemedian", "regionmedian")
            .withColumnRenamed("province_id", "provinceid")
            .withColumnRenamed("name", "provincename")
            .withColumnRenamed("region_id", "regionid")
            .withColumnRenamed("region_name", "regionname")
            .withColumn("provincemedian", col("provincemedian").cast(DataTypes.DoubleType))
            .withColumn("regionmedian", col("regionmedian").cast(DataTypes.DoubleType))
            .write
            .mode(SaveMode.Append)
            .jdbc("jdbc:clickhouse://192.168.45.13:8123/shtd_result", "nationmedian", conf)




//


//        fin
//            .write
//            .mode(SaveMode.Append)
//            .format("hudi")
//            .options(getQuickstartWriteConfigs)
//            .option(SQL_ENABLE_BULK_INSERT.key(), "true")
//            .option(HIVE_STYLE_PARTITIONING.key(), "true")
//            .option(RECORDKEY_FIELD.key(), "uuid")
//            .option(PARTITIONPATH_FIELD.key(), "year, month")
//            .option(PRECOMBINE_FIELD.key(), "total_count")
//            .option(TBL_NAME.key(), "province_consumption_day_aggr")
//            .save("hdfs:///user/hive/warehouse/dws_ds_hudi.db/province_consumption_day_aggr")
    }
}
