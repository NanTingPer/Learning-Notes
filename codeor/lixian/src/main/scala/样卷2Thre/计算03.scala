package 样卷2Thre

object 计算03 {
    def main(args: Array[String]): Unit = {
        import org.apache.hudi.DataSourceWriteOptions._
        import org.apache.hudi.QuickstartUtils._
        import org.apache.hudi.config.HoodieWriteConfig._
        import org.apache.spark.sql.{SaveMode, SparkSession}
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.types.DataTypes

        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
            .appName("dawf")
            .getOrCreate()

        val order_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_info")
            .select("final_total_amount", "create_time", "province_id")

        val oneoredr_info = order_info
            .withColumn("year", year(col("create_time")))
            .withColumn("month", month(col("create_time")))

        val aggtbale = oneoredr_info.groupBy("year", "month", "province_id")
            .agg(count("*") as "total_count", sum("final_total_amount") as "total_amount")
//        aggtbale.show

        val province = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_province").where(col("etl_date") === "20250326").select("id", "name", "region_id")
        val region = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_region").where(col("etl_date") === "20250326").select("id", "region_name")
        val fin = aggtbale
            .join(province, col("province_id") === province("id"))
            .join(region, col("region_id") === region("id"))
            .select("province_id", "name", "region_id", "region_name", "total_amount", "total_count", "year", "month")
            .withColumnRenamed("name", "province_name")
//        fin.show

        val win1 = Window.partitionBy("region_id").orderBy(col("total_amount").desc)
        val fintable = fin.withColumn("sequence", row_number().over(win1))
            .withColumn("uuid", expr("uuid()"))
            .select("uuid", "province_id", "province_name", "region_id", "region_name", "total_amount", "total_count", "sequence", "year", "month")
//        fintable.show

        var fintable1 =fintable
            .withColumn("uuid",col("uuid").cast(DataTypes.StringType))
            .withColumn("province_id",col("province_id").cast(DataTypes.IntegerType))
            .withColumn("province_name",col("province_name").cast(DataTypes.StringType))
            .withColumn("region_id",col("region_id").cast(DataTypes.IntegerType))
            .withColumn("region_name",col("region_name").cast(DataTypes.StringType))
            .withColumn("total_amount",col("total_amount").cast(DataTypes.DoubleType))
            .withColumn("total_count",col("total_count").cast(DataTypes.IntegerType))
            .withColumn("sequence",col("sequence").cast(DataTypes.IntegerType))
            .withColumn("year",col("year").cast(DataTypes.IntegerType))
            .withColumn("month",col("month").cast(DataTypes.IntegerType))
        fintable1
            .write
            .format("hudi")
            .options(getQuickstartWriteConfigs)
            .option(PARTITIONPATH_FIELD.key, "year,month")
            .option(RECORDKEY_FIELD.key, "uuid")
            .option(PRECOMBINE_FIELD.key, "total_count")
            .option(HIVE_STYLE_PARTITIONING.key, "true")
            .option(SQL_ENABLE_BULK_INSERT.key, "true")
            .option(TBL_NAME.key, "province_consumption_day_aggr")
            .mode(SaveMode.Overwrite)
            .save("hdfs:///user/hive/warehouse/dws_ds_hudi.db/province_consumption_day_aggr")
    }
}
