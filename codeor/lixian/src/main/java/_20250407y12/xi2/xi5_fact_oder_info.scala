package _20250407y12.xi2

object xi5_fact_oder_info {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        import org.apache.hudi.DataSourceWriteOptions._
        import org.apache.hudi.QuickstartUtils._
        import org.apache.hudi.config.HoodieWriteConfig._
        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.types.DataTypes
        import org.apache.spark.sql.{SaveMode, SparkSession}

        val spark = SparkSession
            .builder()
            .appName("hive")
          .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
          .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
          .enableHiveSupport()
            .master("local[*]")
            .getOrCreate()

        //ods
        val odstable = spark.read.load("hdfs:///user/hive/warehouse/ods_ds_hudi.db/order_info").where(col("etl_date") === "20250406")
          .drop("_hoodie_commit_time", "_hoodie_commit_seqno", "_hoodie_record_key", "_hoodie_partition_path", "_hoodie_file_name")

        //ods插
        val unionTable = odstable
            .withColumn("dwd_insert_user", lit("user1"))
            .withColumn("dwd_insert_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
            .withColumn("dwd_modify_user", lit("user1"))
            .withColumn("dwd_modify_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))

        unionTable
          .withColumn("operate_time", coalesce(col("operate_time"), col("create_time")))
          .withColumn("operate_time", col("operate_time").cast(DataTypes.TimestampType))
          .withColumn("etl_date", date_format(col("create_time"), "yyyyMMdd"))
            .write
            .mode(SaveMode.Append)
            .format("hudi")
            .options(getQuickstartWriteConfigs)
            .option(SQL_ENABLE_BULK_INSERT.key(), "true")
            .option(HIVE_STYLE_PARTITIONING.key(), "true")
            .option(PARTITIONPATH_FIELD.key(), "etl_date")
            .option(RECORDKEY_FIELD.key(), "id")
            .option(PRECOMBINE_FIELD.key(), "operate_time")
            .option(TBL_NAME.key(), "fact_order_info")
            .save("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_info")

    }
}
