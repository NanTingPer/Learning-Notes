package 样卷2Thre

object 计算02 {
    def main(args: Array[String]): Unit = {
        import org.apache.spark.sql.SparkSession
        import org.apache.spark.sql.functions._
        import org.apache.hudi.QuickstartUtils._
        import org.apache.hudi.DataSourceWriteOptions._
        import org.apache.hudi.config.HoodieWriteConfig._
        import org.apache.spark.sql.SaveMode
        import org.apache.spark.sql.types.DataTypes

        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
            .appName("dawf")
            .getOrCreate()

        val order_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_info")
            .select("user_id", "final_total_amount", "create_time")

        val Onetable = order_info
            .withColumn("year", year(col("create_time")))
            .withColumn("month", month(col("create_time")))
            .withColumn("day", date_format(col("create_time"), "dd"))


        val aggtable = Onetable
            .groupBy("user_id", "year", "month", "day")
            .agg(count("*") as "total_count", sum("final_total_amount") as "total_amount")
            .withColumn("uuid", expr("uuid()"))


        val user_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_user_info")
            .where(col("etl_date") === "20250326")
            .select("name", "id")


        val fin = aggtable.join(user_info, col("user_id") === user_info("id"))
            .select("uuid", "user_id", "name", "total_amount", "total_count", "year", "month", "day")
            .withColumnRenamed("name", "user_name")
            .withColumn("user_id", col("user_id").cast(DataTypes.IntegerType))
            .withColumn("user_name", col("user_name").cast(DataTypes.StringType))
            .withColumn("total_amount", col("total_amount").cast(DataTypes.DoubleType))
            .withColumn("total_count", col("total_count").cast(DataTypes.IntegerType))
            .withColumn("year", col("year").cast(DataTypes.IntegerType))
            .withColumn("month", col("month").cast(DataTypes.IntegerType))
            .withColumn("day", col("day").cast(DataTypes.IntegerType))
        fin.show()
        fin
            .write
            .format("hudi")
            .options(getQuickstartWriteConfigs)
            .option(PARTITIONPATH_FIELD.key, "year,month,day")
            .option(RECORDKEY_FIELD.key, "uuid")
            .option(PRECOMBINE_FIELD.key, "total_count")
            .option(HIVE_STYLE_PARTITIONING.key, "true")
            .option(SQL_ENABLE_BULK_INSERT.key, "true")
            .option(TBL_NAME.key, "user_consumption_day_aggr2")
            .mode(SaveMode.Overwrite)
            .save("hdfs:///user/hive/warehouse/dws_ds_hudi.db/user_consumption_day_aggr2")

        spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dws_ds_hudi.db/user_consumption_day_aggr2").show
    }
}
