package 样卷2Thre.清洗

object 清洗06 {
    def main(args: Array[String]): Unit = {
        import org.apache.hudi.DataSourceWriteOptions
        import org.apache.hudi.QuickstartUtils._
        import org.apache.hudi.config.HoodieWriteConfig.TBL_NAME
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.types.DataTypes
        import org.apache.spark.sql.{SaveMode, SparkSession}

        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
            .master("yarn")
            .appName("awfawf")
            .getOrCreate()
        val odsdata = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/ods_ds_hudi.db/order_detail").where(col("etl_date") === "20250326")
            .drop("_hoodie_commit_time","_hoodie_commit_seqno","_hoodie_record_key","_hoodie_partition_path","_hoodie_file_name")

        val dwddata = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_detail")
            .drop("_hoodie_commit_time","_hoodie_commit_seqno","_hoodie_record_key","_hoodie_partition_path","_hoodie_file_name")
        val cols = dwddata.columns.map(col)

        odsdata
            .withColumn("dwd_insert_user", lit("user1"))
            .withColumn("dwd_insert_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
            .withColumn("dwd_modify_user", lit("user1"))
            .withColumn("dwd_modify_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
            .withColumn("etl_date", date_format(col("create_time"),"yyyyMMdd"))
            .select(cols: _*)
            .write
            .mode(SaveMode.Append)
            .format("hudi")
            .options(getQuickstartWriteConfigs)
            .option(DataSourceWriteOptions.RECORDKEY_FIELD.key(),"id")
            .option(DataSourceWriteOptions.PRECOMBINE_FIELD.key(),"dwd_modify_time")
            .option(DataSourceWriteOptions.PARTITIONPATH_FIELD.key(),"etl_date")
            .option(DataSourceWriteOptions.HIVE_STYLE_PARTITIONING.key(),"true")
            .option(DataSourceWriteOptions.SQL_ENABLE_BULK_INSERT.key(),"true")
            .mode(SaveMode.Append)
            .option(TBL_NAME.key(),"fact_order_detail")
            .save("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_detail")
    }
}
