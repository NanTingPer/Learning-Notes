package 样卷2

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.hudi.config._
import org.apache.hudi.config.HoodieWriteConfig
import org.apache.spark.sql.hudi._
import org.apache.hudi.DataSourceWriteOptions._
import org.apache.hudi.QuickstartUtils._


import java.util.Properties

object 抽取02 {
    def main(args: Array[String]): Unit = {
        val spark = SparkSession
            .builder()
            .master("local[*]")
            .appName("chouqu01")
            .enableHiveSupport()
            .config("spark.serializer", "org.apache.spark.serializer.KyreSerializer")
            .config("spark.sql.extension", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
            .getOrCreate()

//        val huditable = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/ods_ds_hudi.db/sku_info")
//            .drop("_hoodie_commit_time","_hoodie_commit_seqno","_hoodie_record_key","_hoodie_partition_path","_hoodie_file_name")

//        huditable.show()
//        val cols = huditable.columns.map(col)
        //_hoodie_commit_time	string	NULL
        //_hoodie_commit_seqno	string	NULL
        //_hoodie_record_key	string	NULL
        //_hoodie_partition_path	string	NULL
        //_hoodie_file_name	string	NULL

        //create_time作为增量字段
//        val maxetl = huditable.select(max("etl_date")).first()(0)
//        val maxTime = huditable.where(col("etl_date") === maxetl).select(max("create_time")).first()(0)

        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")

        //增量数据
        val zlsj = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "sku_info", conf)
//            .where(col("create_time") > maxTime)
            .withColumn("etl_date", lit("20250324"))
//            .select(cols:_*)
            .write
            .format("hudi")
            .options(getQuickstartWriteConfigs)
            .option(RECORDKEY_FIELD.key,"id")
            .option(PARTITIONPATH_FIELD.key, "etl_date")
            .option(PRECOMBINE_FIELD.key, "create_time")
            .option(HoodieWriteConfig.TBL_NAME.key,"sku_info")
            .mode(SaveMode.Overwrite)
            .save("hdfs:///user/hive/warehouse/ods_ds_hudi.db/sku_info")
    }
}
