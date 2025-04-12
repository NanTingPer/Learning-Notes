package _2025Two.QX

object QX6 {
    def main(args: Array[String]): Unit = {
        import org.apache.hudi.DataSourceWriteOptions._
        import org.apache.hudi.QuickstartUtils._
        import org.apache.hudi.config.HoodieWriteConfig._
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.types.DataTypes
        import org.apache.spark.sql.{SaveMode, SparkSession}
        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .appName("hudi")
            .getOrCreate()

        val ods_table_name = "order_detail"
        val dwd_table_name = "fact_order_detail"
        val pre_combine_field = "dwd_modify_time"

        //ods
        val ods = spark.read.format("hudi").load(s"hdfs:///user/hive/warehouse/ods_ds_hudi.db/${ods_table_name}").where(col("etl_date") === "20250410")
            .drop("_hoodie_commit_time")
            .drop("_hoodie_commit_seqno")
            .drop("_hoodie_record_key")
            .drop("_hoodie_partition_path")
            .drop("_hoodie_file_name")
            .withColumn("etl_date", date_format(col("create_time"), "yyyyMMdd"))
            .withColumn("dwd_insert_user",lit("user1"))
            .withColumn("dwd_insert_time",date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
            .withColumn("dwd_modify_user",lit("user1"))
            .withColumn("dwd_modify_time",date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
            .withColumn(pre_combine_field, col(pre_combine_field).cast(DataTypes.TimestampType))
            .write
            .format("hudi")
            .mode(SaveMode.Append)
            .options(getQuickstartWriteConfigs)
            .option(SQL_ENABLE_BULK_INSERT.key,"true")
            .option(HIVE_STYLE_PARTITIONING.key, "true")
            .option(TBL_NAME.key, dwd_table_name)
            .option(RECORDKEY_FIELD.key, "id")
            .option(PRECOMBINE_FIELD.key, pre_combine_field)
            .option(PARTITIONPATH_FIELD.key, "etl_date")
            .save(s"hdfs:///user/hive/warehouse/dwd_ds_hudi.db/${dwd_table_name}")




    }
}
