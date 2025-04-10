package _2025One1.qx



object qx1 {
    def main(args: Array[String]): Unit = {
        import org.apache.hudi.DataSourceWriteOptions._
        import org.apache.hudi.QuickstartUtils._
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.{SaveMode, SparkSession}
        import org.apache.spark.sql.types.DataTypes

        import java.util.Properties
        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .appName("hudi")
            .getOrCreate()

        val ods_table = "user_info"
        val dwd_table = "dim_user_info"

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
        //up data
        val new_data = dwd.select(max("etl_date")).first()(0)
        val dwd_data = dwd.where(col("etl_date") === new_data)

        //ods insert
        val union_table = ods
            .withColumn("dwd_insert_user", lit("user1"))
            .withColumn("dwd_insert_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
            .withColumn("dwd_modify_user", lit("user1"))
            .withColumn("dwd_modify_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
            .union(dwd_data)

        //heb
        val win1 = Window.partitionBy("id").orderBy(col("operate_time").desc)
        val win2 = Window.partitionBy("id")
        union_table
            .withColumn("row_num", row_number().over(win1))
            .withColumn("dwd_insert_time", min(col("dwd_insert_time")).over(win2))
            .withColumn("dwd_modify_time", max(col("dwd_modify_time")).over(win2))
            .where(col("row_num") === 1)
            .drop("row_num")
            .withColumn("etl_date", lit("20250409"))
            .write
            .format("hudi")
            .mode(SaveMode.Append)
            .options(getQuickstartWriteConfigs)
            .option(SQL_ENABLE_BULK_INSERT.key, "true")
            .option(HIVE_STYLE_PARTITIONING.key, "true")
            .option(RECORDKEY_FIELD.key, "id")
            .option(PARTITIONPATH_FIELD.key, "etl_date")
            .option(PRECOMBINE_FIELD.key, "operate_time")
            .save(s"hdfs:///user/hive/warehouse/dwd_ds_hudi.db/${dwd_table}")

    }
}
