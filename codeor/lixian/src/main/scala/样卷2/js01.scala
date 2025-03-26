package yg2

import org.apache.hudi.DataSourceWriteOptions._
import org.apache.hudi.config.HoodieWriteConfig._
import org.apache.hudi.QuickstartUtils._
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions._

import java.util.UUID


object js01 {
    def main(args: Array[String]): Unit = {
        val spark = SparkSession
            .builder()
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .config("spark.sql.extension", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
            .enableHiveSupport()
            .master("yarn")
            .appName("HELLLDAWFWAF:WAFFFFFFF")
            .getOrCreate()

        val order_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_info")
//        val sku_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_sku_info")
//        val dim_province = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_province")
//        val dim_region = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_region")
        val user_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_user_info").select("id", "name")

        //2020-04-26 02:51:13
        val oneOrder_info = order_info.select(col("user_id"), col("final_total_amount"), col("create_time"))
        val twoOrder_info = oneOrder_info
            .withColumn("year", year(col("create_time")))
            .withColumn("month", month(col("create_time")))
            .withColumn("day", date_format(col("create_time"), "dd"))

        val aggOredr_info = twoOrder_info
            .groupBy("year", "month", "day", "user_id")
            .agg(count("*") as "total_count", sum("final_total_amount") as "total_amount")

        aggOredr_info.show
        val frat = aggOredr_info
            .join(user_info, aggOredr_info("user_id") === user_info("id"))
            .withColumn("uuid", expr("uuid()"))
            .select(col("uuid"), col("user_id"), col("name") as "user_name", col("total_amount"), col("total_count"), col("year"), col("month"), col("day"))

        frat
            .write
            .format("hudi")
            .options(getQuickstartWriteConfigs)
            .option(HIVE_STYLE_PARTITIONING.key, "true")
            .option(SQL_ENABLE_BULK_INSERT.key, "true")
            .option(TBL_NAME.key, "user_consumption_day_aggr")
            .option(RECORDKEY_FIELD.key,"uuid")
            .option(PRECOMBINE_FIELD.key, "total_count")
            .option(PARTITIONPATH_FIELD.key, "year")
            .option(PARTITIONPATH_FIELD.key, "month")
            .option(PARTITIONPATH_FIELD.key, "day")
            .mode(SaveMode.Overwrite)
            .save("hdfs:///user/hive/warehouse/dws_ds_hudi.db/user_consumption_day_aggr")


    }
}
