package _2025Two.QX

object QX1 {
    def main(args: Array[String]): Unit = {
        import org.apache.hudi.DataSourceWriteOptions._
        import org.apache.hudi.QuickstartUtils._
        import org.apache.hudi.config.HoodieWriteConfig._
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.types.DataTypes
        import org.apache.spark.sql.{SaveMode, SparkSession}

        import java.util.Properties
        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .appName("hudi")
            .getOrCreate()

        val ods_table_name = "user_info"
        val dwd_table_name = "dim_user_info"
        val pre_combine_field = "operate_time"

        //ods
        val ods = spark.read.format("hudi").load(s"hdfs:///user/hive/warehouse/ods_ds_hudi.db/${ods_table_name}").where(col("etl_date") === "20250410")
            .drop("_hoodie_commit_time")
            .drop("_hoodie_commit_seqno")
            .drop("_hoodie_record_key")
            .drop("_hoodie_partition_path")
            .drop("_hoodie_file_name")

        //dwd
        val dwd = spark.read.format("hudi").load(s"hdfs:///user/hive/warehouse/dwd_ds_hudi.db/${dwd_table_name}")
        val dwd_max_time = dwd.select(max("etl_date")).first()(0)
        val yx_dwd = dwd.where(col("etl_date") === dwd_max_time)
            .drop("_hoodie_commit_time")
            .drop("_hoodie_commit_seqno")
            .drop("_hoodie_record_key")
            .drop("_hoodie_partition_path")
            .drop("_hoodie_file_name")

        val cols = yx_dwd.columns.map(col)

        val un_table = ods
            .withColumn("dwd_insert_user", lit("user1"))
            .withColumn("dwd_insert_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
            .withColumn("dwd_modify_user", lit("user1"))
            .withColumn("dwd_modify_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
            .select(cols: _*)
            .union(yx_dwd)

        val win1 = Window.partitionBy("id").orderBy(col("operate_time").desc)
        val win2 = Window.partitionBy("id")

        un_table
            .withColumn("temp", row_number().over(win1))
            .withColumn("dwd_insert_time", min("dwd_insert_time").over(win2))
            .withColumn("dwd_modify_time", max("dwd_modify_time").over(win2))
            .withColumn("etl_date", lit("20250410"))
            .withColumn(pre_combine_field, col(pre_combine_field).cast(DataTypes.TimestampType))
            .where(col("temp") === 1)
            .drop("temp")
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
