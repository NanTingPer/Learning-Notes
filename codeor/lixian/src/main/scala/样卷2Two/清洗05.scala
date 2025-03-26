package 样卷2Two

object 清洗05 {
    def main(args: Array[String]): Unit = {
        import org.apache.hudi.DataSourceWriteOptions._
        import org.apache.hudi.QuickstartUtils._
        import org.apache.hudi.config.HoodieWriteConfig.TBL_NAME
        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.{SaveMode, SparkSession}
        val spark = SparkSession
            .builder()
            .master("local[*]")
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .config("spark.sql.extension", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
            .enableHiveSupport()
            .getOrCreate()

        var hudidata = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/ods_ds_hudi.db/order_info").where(col("etl_date") === "20250325")
            .drop("_hoodie_commit_time","_hoodie_commit_seqno","_hoodie_record_key","_hoodie_partition_path","_hoodie_file_name")
            .withColumn("operate_time", coalesce(col("operate_time"),col("create_time")))

        var dimdata = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_info")
        val newPartition = dimdata.select(max("etl_date")).first()(0)
        dimdata = dimdata.where(col("etl_date") === newPartition)
            .drop("_hoodie_commit_time","_hoodie_commit_seqno","_hoodie_record_key","_hoodie_partition_path","_hoodie_file_name")

        val cols = dimdata.columns.map(col)

         hudidata
            .withColumn("dwd_insert_user", lit("user1"))
            .withColumn("dwd_insert_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss"))
            .withColumn("dwd_modify_user", lit("user1"))
            .withColumn("dwd_modify_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss"))
            .select(cols: _*)
            .withColumn("etl_date",date_format(col("create_time"), "yyyyMMdd"))
            .write
            .format("hudi")
            .options(getQuickstartWriteConfigs)
            .option(RECORDKEY_FIELD.key,"id")
            .option(PRECOMBINE_FIELD.key,"operate_time")
            .option(PARTITIONPATH_FIELD.key,"etl_date")
             .option(HIVE_STYLE_PARTITIONING.key,"true")
             .option(SQL_ENABLE_BULK_INSERT.key,"true")
            .option(TBL_NAME.key, "fact_order_info")
            .mode(SaveMode.Append)
            .save("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_info")

    }
}
