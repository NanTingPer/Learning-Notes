package 样卷2

import org.apache.hudi.DataSourceWriteOptions.{HIVE_STYLE_PARTITIONING, PARTITIONPATH_FIELD, PRECOMBINE_FIELD, RECORDKEY_FIELD, SQL_ENABLE_BULK_INSERT}
import org.apache.hudi.QuickstartUtils.getQuickstartWriteConfigs
import org.apache.hudi.config.HoodieWriteConfig
import org.apache.spark.sql.expressions._
import org.apache.spark.sql.functions.{col, _}
import org.apache.spark.sql.{SaveMode, SparkSession}

object region {
    def main(args: Array[String]): Unit = {
        import org.apache.hudi.DataSourceWriteOptions.{HIVE_STYLE_PARTITIONING, PARTITIONPATH_FIELD, PRECOMBINE_FIELD, RECORDKEY_FIELD, SQL_ENABLE_BULK_INSERT}
        import org.apache.hudi.QuickstartUtils.getQuickstartWriteConfigs
        import org.apache.hudi.config.HoodieWriteConfig
        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.functions.{col, _}
        import org.apache.spark.sql.{SaveMode, SparkSession}
        val spark = SparkSession
            .builder()
            .master("local[*]")
            .appName("chouqu01")
            .enableHiveSupport()
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .config("spark.sql.extension", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
            .getOrCreate()
//        ods_ds_hudi库中user_info


        val huditable = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/ods_ds_hudi.db/base_region")
            .drop("_hoodie_commit_time","_hoodie_commit_seqno","_hoodie_record_key","_hoodie_partition_path","_hoodie_file_name")
            .where(col("etl_date") === lit("20250324") or col("etl_date") === lit("20250325") or col("etl_date") === lit("20250326"))
        val odstable = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_region")
            .drop("_hoodie_commit_time", "_hoodie_commit_seqno", "_hoodie_record_key", "_hoodie_partition_path", "_hoodie_file_name")

//
//        odstable.show()
//
        val cols = odstable.columns.map(col)
//        val odsmaxetl_date = odstable.select(max("etl_date")).first()(0)
//        val odsdata = odstable.where(col("etl_date") === odsmaxetl_date)

        val win1 = Window.partitionBy("id").orderBy(col("create_time").desc)
        val win2 = Window.partitionBy("id")

        val hudinewtabke = huditable
            .withColumn("dwd_insert_user", lit("user1"))
            .withColumn("dwd_insert_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss"))
            .withColumn("dwd_modify_user", lit("user1"))
            .withColumn("dwd_modify_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss"))
            .select(cols: _*)

        val un_t = hudinewtabke
//            .union(odsdata)
            .withColumn("temp", row_number().over(win1))
            .withColumn("dwd_insert_time", min("dwd_insert_time").over(win2))
            .withColumn("dwd_modify_time", max("dwd_insert_time").over(win2))
            .where(col("temp") === 1)
            .drop("temp")

        un_t
            .write
            .format("hudi")
            .options(getQuickstartWriteConfigs)
            .option(PRECOMBINE_FIELD.key,"dwd_modify_time")
            .option(PARTITIONPATH_FIELD.key, "etl_date")
            .option(RECORDKEY_FIELD.key, "id")
            .option(HIVE_STYLE_PARTITIONING.key, "true")
            .option(SQL_ENABLE_BULK_INSERT.key, "true")
            .option(HoodieWriteConfig.TBL_NAME.key,"dim_region")
            .mode(SaveMode.Overwrite)
            .save("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_region")
    }
}
