package hudi.one

//--conf "spark.sql.extensions=org.apache.spark.sql.hudi.HoodieSparkSessionExtension" \
//--conf "spark.serializer=org.apache.spark.serializer.KryoSerializer" \
object qx6 {
    def main(args: Array[String]): Unit = {
        import org.apache.hudi.DataSourceWriteOptions._
        import org.apache.hudi.QuickstartUtils._
        import org.apache.hudi.config.HoodieWriteConfig._
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.types.DataTypes
        import org.apache.spark.sql.{SaveMode, SparkSession}
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .master("local[*]")
            .getOrCreate()

        //ods_ds_hudi
        val user_info = spark
            .read
            .format("hudi")
            .load("hdfs:///user/hive/warehouse/ods_ds_hudi.db/order_detail")
            .drop("_hoodie_commit_time")
            .drop("_hoodie_commit_seqno")
            .drop("_hoodie_record_key")
            .drop("_hoodie_partition_path")
            .drop("_hoodie_file_name")
            .where(col("etl_date") === "20250407")
            .withColumn("dwd_insert_user", lit("user1"))
            .withColumn("dwd_insert_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
            .withColumn("dwd_modify_user", lit("user1"))
            .withColumn("dwd_modify_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
            .withColumn("etl_date", date_format(col("create_time"), "yyyyMMdd"))
            .write
            .mode(SaveMode.Append)
            .format("hudi")
            .options(getQuickstartWriteConfigs)
            .option(SQL_ENABLE_BULK_INSERT.key(), "true")
            .option(HIVE_STYLE_PARTITIONING.key(), "true")
            .option(RECORDKEY_FIELD.key(), "id")
            .option(PARTITIONPATH_FIELD.key(), "etl_date")
            .option(PRECOMBINE_FIELD.key(), "dwd_modify_time")
            .option(TBL_NAME.key(), "fact_order_detail")
            .save("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_detail")
    }
}
