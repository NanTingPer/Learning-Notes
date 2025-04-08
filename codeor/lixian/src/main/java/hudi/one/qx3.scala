package hudi.one

//--conf "spark.sql.extensions=org.apache.spark.sql.hudi.HoodieSparkSessionExtension" \
//--conf "spark.serializer=org.apache.spark.serializer.KryoSerializer" \
object qx3 {
    def main(args: Array[String]): Unit = {
        import org.apache.hudi.DataSourceWriteOptions._
        import org.apache.hudi.QuickstartUtils._
        import org.apache.hudi.config.HoodieWriteConfig._
        import org.apache.spark.sql.expressions._
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
            .load("hdfs:///user/hive/warehouse/ods_ds_hudi.db/base_province")
            .drop("_hoodie_commit_time")
            .drop("_hoodie_commit_seqno")
            .drop("_hoodie_record_key")
            .drop("_hoodie_partition_path")
            .drop("_hoodie_file_name")
            .where(col("etl_date") === "20250407")

        //dwd_ds_hudi
        val dwd = spark
            .read
            .format("hudi")
            .load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_province")
            .drop("_hoodie_commit_time")
            .drop("_hoodie_commit_seqno")
            .drop("_hoodie_record_key")
            .drop("_hoodie_partition_path")
            .drop("_hoodie_file_name")

        val dwdMaxPartition = dwd.select(max("etl_date")).first()(0)
        val yxdwd = dwd.where(col("etl_date") === dwdMaxPartition)
        val cols = yxdwd.columns.map(col)


        val untable = user_info
            .withColumn("dwd_insert_user", lit("user1"))
            .withColumn("dwd_insert_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
            .withColumn("dwd_modify_user", lit("user1"))
            .withColumn("dwd_modify_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
            .select(cols: _*)
            .union(yxdwd)

        val win1 = Window.partitionBy("id").orderBy(col("create_time").desc)
        val win2 = Window.partitionBy("id")
        untable
            .withColumn("temp", row_number().over(win1))
            .withColumn("dwd_insert_time", min("dwd_insert_time").over(win2))
            .withColumn("dwd_modify_time", max("dwd_modify_time").over(win2))
            .where(col("temp") === 1)
            .drop("temp")
            .withColumn("etl_date", lit("20250407"))
            .write
            .mode(SaveMode.Append)
            .format("hudi")
            .options(getQuickstartWriteConfigs)
            .option(SQL_ENABLE_BULK_INSERT.key(), "true")
            .option(HIVE_STYLE_PARTITIONING.key(), "true")
            .option(RECORDKEY_FIELD.key(), "id")
            .option(PARTITIONPATH_FIELD.key(), "etl_date")
            .option(PRECOMBINE_FIELD.key(), "dwd_modify_time")
            .option(TBL_NAME.key(), "dim_province")
            .save("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_province")
    }
}
