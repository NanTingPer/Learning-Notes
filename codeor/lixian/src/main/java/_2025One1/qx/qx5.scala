package _2025One1.qx

object qx5 {
    def main(args: Array[String]): Unit = {
        import org.apache.hudi.DataSourceWriteOptions._
        import org.apache.hudi.QuickstartUtils._
        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.types.DataTypes
        import org.apache.spark.sql.{SaveMode, SparkSession}
        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .appName("hudi")
            .getOrCreate()

        val ods_table = "order_info"
        val dwd_table = "fact_order_info"
        val pre_combine_field = "operate_time"

        //ods
        val ods = spark.read.format("hudi").load(s"hdfs:///user/hive/warehouse/ods_ds_hudi.db/${ods_table}")
            .where(col("etl_date") === "20250409")
            .drop("_hoodie_commit_time")
            .drop("_hoodie_commit_seqno")
            .drop("_hoodie_record_key")
            .drop("_hoodie_partition_path")
            .drop("_hoodie_file_name")

        //dwd
        val dwd = spark.read.format("hudi").load(s"hdfs:///user/hive/warehouse/dwd_ds_hudi.db/${dwd_table}")
            .drop("_hoodie_commit_time")
            .drop("_hoodie_commit_seqno")
            .drop("_hoodie_record_key")
            .drop("_hoodie_partition_path")
            .drop("_hoodie_file_name")
        val cols = dwd.columns.map(col)

        //ods insert
        ods
            .withColumn("operate_time", coalesce(col("operate_time"), col("create_time")))
            .withColumn("dwd_insert_user", lit("user1"))
            .withColumn("dwd_insert_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
            .withColumn("dwd_modify_user", lit("user1"))
            .withColumn("dwd_modify_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
            .withColumn("etl_date", date_format(col("create_time"),"yyyyMMdd"))
            .withColumn("operate_time", col("operate_time").cast(DataTypes.TimestampType))
            .select(cols:_*)
            .write
            .format("hudi")
            .mode(SaveMode.Append)
            .options(getQuickstartWriteConfigs)
            .option(SQL_ENABLE_BULK_INSERT.key, "true")
            .option(HIVE_STYLE_PARTITIONING.key, "true")
            .option(RECORDKEY_FIELD.key, "id")
            .option(PARTITIONPATH_FIELD.key, "etl_date")
            .option(PRECOMBINE_FIELD.key, pre_combine_field)
            .save(s"hdfs:///user/hive/warehouse/dwd_ds_hudi.db/${dwd_table}")

    }
}
