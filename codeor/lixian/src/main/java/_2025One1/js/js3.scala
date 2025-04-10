package _2025One1.js

import java.util.Properties

object js3 {
    def main(args: Array[String]): Unit = {
        import org.apache.hudi.DataSourceWriteOptions._
        import org.apache.hudi.QuickstartUtils._
        import org.apache.hudi.config.HoodieWriteConfig._
        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.{SaveMode, SparkSession}
        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .appName("hudi")
            .getOrCreate()

        val region = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_region").where(col("etl_date") === "20250409")
        val province = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_province").where(col("etl_date") === "20250409")
        val order_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_info")
        val order_detail = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_detail")
        val user_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_user_info").where(col("etl_date") === "20250409")


        val yes_order = order_detail
            .where(year(col("create_time")) === 2020)

        //销售量
        val topquantity = yes_order
            .groupBy("sku_id", "sku_name")
            .agg(count("*") as "topquantity")
            .withColumn("sequence", row_number().over(Window.orderBy(col("topquantity").desc)))
            .withColumnRenamed("sku_id", "topquantityid")
            .withColumnRenamed("sku_name", "topquantityname")

        //销售额
        val topprice = yes_order
            .withColumn("xiao_shou_e", col("sku_num") * col("order_price"))
            .groupBy("sku_id", "sku_name")
            .agg(sum("xiao_shou_e") as "topprice")
            .withColumnRenamed("sku_id", "toppriceid")
            .withColumnRenamed("sku_name", "toppricename")
            .withColumn("sequence", row_number().over(Window.orderBy(col("topprice").desc)))

        val conf = new Properties()
        conf.put("user", "default")
        conf.put("password", "123456")
        topquantity
            .join(topprice, Seq("sequence"))
            .select("topquantityid", "topquantityname", "topquantity", "toppriceid", "toppricename", "topprice", "sequence")
            .write
            .mode(SaveMode.Append)
            .jdbc("jdbc:clickhouse://192.168.45.13:8123/shtd_result", "topten", conf)

//            .write
//            .format("hudi")
//            .mode(SaveMode.Append)
//            .options(getQuickstartWriteConfigs)
//            .option(SQL_ENABLE_BULK_INSERT.key, "true")
//            .option(HIVE_STYLE_PARTITIONING.key, "true")
//            .option(RECORDKEY_FIELD.key, "uuid")
//            .option(PARTITIONPATH_FIELD.key, "year,month")
//            .option(PRECOMBINE_FIELD.key, "total_count")
//            .option(TBL_NAME.key, "province_consumption_day_aggr")
//            .save(s"hdfs:///user/hive/warehouse/dws_ds_hudi.db/province_consumption_day_aggr")

    }
}
