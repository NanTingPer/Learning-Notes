package 样卷2Two

object 清洗01 {
    def main(args: Array[String]): Unit = {
        import org.apache.spark.sql.SparkSession
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.expressions._
        import org.apache.hudi.config.HoodieWriteConfig.TBL_NAME
        import org.apache.hudi.DataSourceWriteOptions._
        import org.apache.hudi.QuickstartUtils._
        import org.apache.spark.sql.SaveMode
        import java.util.Properties
        val spark = SparkSession
            .builder()
            .master("local[*]")
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .config("spark.sql.extension", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
            .enableHiveSupport()
            .getOrCreate()

        var hudidata = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/ods_ds_hudi.db/user_info").where(col("etl_date") === "20250325")
            .drop("_hoodie_commit_time","_hoodie_commit_seqno","_hoodie_record_key","_hoodie_partition_path","_hoodie_file_name")

        var dimdata = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_user_info")
        val newPartition = dimdata.select(max("etl_date")).first()(0)
        dimdata = dimdata.where(col("etl_date") === newPartition)
            .drop("_hoodie_commit_time","_hoodie_commit_seqno","_hoodie_record_key","_hoodie_partition_path","_hoodie_file_name")
//        hudidata.show()
//        dimdata.show()
        val cols = dimdata.columns.map(col)

        hudidata = hudidata
            .withColumn("dwd_insert_user", lit("user1"))
            .withColumn("dwd_insert_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss"))
            .withColumn("dwd_modify_user", lit("user1"))
            .withColumn("dwd_modify_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss"))
            .select(cols: _*)
        hudidata.show()

        val win1 = Window.partitionBy("id").orderBy(col("operate_time").desc)
        val win2 = Window.partitionBy("id")
        hudidata = hudidata.union(dimdata)
            .withColumn("temp", row_number().over(win1))
            .withColumn("dwd_insert_time", min(col("dwd_insert_time")).over(win2))
            .withColumn("dwd_modify_time", max(col("dwd_modify_time")).over(win2))
            .withColumn("etl_date",lit("20250325"))
            .where(col("temp") === 1)
            .drop("temp")
        hudidata.show()
        hudidata
            .write
            .format("hudi")
            .options(getQuickstartWriteConfigs)
            .option(RECORDKEY_FIELD.key,"id")
            .option(PRECOMBINE_FIELD.key,"operate_time")
            .option(PARTITIONPATH_FIELD.key,"etl_date")
            .option(HIVE_STYLE_PARTITIONING.key,"true")
            .option(SQL_ENABLE_BULK_INSERT.key,"true")
            .option(TBL_NAME.key, "dim_user_info")
            .mode(SaveMode.Append)
            .save("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_user_info")
    }
}
