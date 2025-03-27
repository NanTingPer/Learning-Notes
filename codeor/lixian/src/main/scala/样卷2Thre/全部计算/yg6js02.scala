package 样卷2Thre.全部计算

import org.apache.hudi.DataSourceWriteOptions.{HIVE_STYLE_PARTITIONING, PARTITIONPATH_FIELD, PRECOMBINE_FIELD, RECORDKEY_FIELD, SQL_ENABLE_BULK_INSERT}
import org.apache.hudi.QuickstartUtils.getQuickstartWriteConfigs
import org.apache.hudi.config.HoodieWriteConfig.TBL_NAME
import org.apache.spark.sql.SaveMode

object yg6js02 {
    def main(args: Array[String]): Unit = {
        import org.apache.spark.sql.SparkSession
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.expressions._
        val spark = SparkSession.builder().enableHiveSupport().appName("wfawfawf")
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
            .getOrCreate()

        val order_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_info")
        val province = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_province").where(col("etl_date") === "20250326")
        val region = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_region").where(col("etl_date") === "20250326")

        val oneorder_info = order_info.select("province_id", "final_total_amount", "create_time")
            .withColumn("year", year(col("create_time")))
            .withColumn("month", month(col("create_time")))


        val win1 = Window.partitionBy("year","month","region_id").orderBy(col("total_amount").desc)
        val table = oneorder_info
            .groupBy("year", "month", "province_id")
            .agg(count("*") as "total_count", sum("final_total_amount") as "total_amount")
            .join(province, province("id") === col("province_id"))
            .join(region, col("region_id") === region("id"))
            .select("province_id", "name", "region_id", "region_name", "total_amount", "total_count", "year", "month")
            .withColumnRenamed("name", "province_name")
            .withColumn("uuid", expr("uuid()"))
            .withColumn("sequence", row_number().over(win1))
            .select("uuid","province_id","province_name","region_id","region_name","total_amount","total_count","sequence","year","month")
            .write
            .format("hudi")
            .options(getQuickstartWriteConfigs)
            .option(PARTITIONPATH_FIELD.key, "year,month")
            .option(RECORDKEY_FIELD.key, "uuid")
            .option(PRECOMBINE_FIELD.key, "total_count")
            .option(HIVE_STYLE_PARTITIONING.key, "true")
            .option(SQL_ENABLE_BULK_INSERT.key, "true")
            .option(TBL_NAME.key, "province_consumption_day_aggr")
            .mode(SaveMode.Overwrite)
            .save("hdfs:///user/hive/warehouse/dws_ds_hudi.db/province_consumption_day_aggr")



    }
}
