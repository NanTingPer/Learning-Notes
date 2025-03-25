package hudi.Temp

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions.{coalesce, col, greatest}
import org.apache.spark.sql.functions._
import org.apache.hudi.config.HoodieWriteConfig
import org.apache.hudi.QuickstartUtils._
import org.apache.hudi.DataSourceWriteOptions._


import java.util.Properties

object Read {
    def main(args: Array[String]): Unit = {
        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .config("spark.sql.extension", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
            .master("local[*]")
            .appName("tempdddd")
            .getOrCreate()

        val huditable = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/ods_ds_hudi.db/user_info")
            .drop("_hoodie_commit_time", "_hoodie_commit_seqno", "_hoodie_record_key", "_hoodie_partition_path", "_hoodie_file_name")

        val huditablemaxtime = huditable.select(max(greatest(col("operate_time"),col("create_time")))).first()(0)
        val cols = huditable.columns.map(col)
        huditable.limit(10).show

        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        var sqltable = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "user_info", conf)
            .withColumn("operate_time", coalesce(col("operate_time"), col("create_time")))

        //增量数据
        sqltable = sqltable.where(greatest(col("operate_time"), col("create_time")) > huditablemaxtime)
            .withColumn("etl_date", lit("20250324"))
            .select(cols:_*)

        sqltable
            .write
            .format("hudi")
            .options(getQuickstartWriteConfigs)
            .option(HoodieWriteConfig.TBL_NAME.key, "user_info")
            .option(PARTITIONPATH_FIELD.key, "etl_date")
            .option(PRECOMBINE_FIELD.key, "id")
            .option(RECORDKEY_FIELD.key,"operate_time")
            .mode(SaveMode.Append)

        sqltable.show()

    }
}
