package _2025Two.JS


object JS2 {
    def main(args: Array[String]): Unit = {
        import java.util.Properties
        import org.apache.hudi.DataSourceWriteOptions._
        import org.apache.hudi.QuickstartUtils._
        import org.apache.hudi.config.HoodieWriteConfig._
        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.types.DataTypes
        import org.apache.spark.sql.{SaveMode, SparkSession}
        //--conf "spark.sql.extensions=org.apache.spark.sql.hudi.HoodieSparkSessionExtension" \
        //--conf "spark.serializer=org.apache.spark.serializer.KryoSerializer" \

        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .appName("hudi")
            .getOrCreate()

        val user_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_user_info").where(col("etl_date") === "20250410")
        val region = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_region").where(col("etl_date") === "20250410")
        val province = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_province").where(col("etl_date") === "20250410")
        val order_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_info")
        val mysqlConf = new Properties()
        mysqlConf.put("user", "root")
        mysqlConf.put("password", "123456")
        //        val order_detail = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_detail")
        val order_detail = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "order_detail", mysqlConf)
            .where(year(col("create_time")) === 2020)
        //xl_top_ten
        val sl_top_ten = order_detail
            .groupBy("sku_id", "sku_name")
            .agg(count("*") as "topquantity")
            .withColumnRenamed("sku_id", "topquantityid")
            .withColumnRenamed("sku_name", "topquantityname")
            .withColumn("sequence", row_number().over(Window.orderBy(col("topquantity").desc)))

        //xsr_top_ten
        val xsr_top_ten = order_detail
            .withColumn("jine", col("sku_num") * col("order_price"))
            .groupBy("sku_id", "sku_name")
            .agg(sum("jine") as "topprice")
            .withColumnRenamed("sku_id", "toppriceid")
            .withColumnRenamed("sku_name", "toppricename")
            .withColumn("sequence", row_number().over(Window.orderBy(col("topprice").desc)))

        val conf = new Properties()
        conf.put("user","default")
        conf.put("password", "123456")
        sl_top_ten.join(xsr_top_ten, Seq("sequence"))
            .select("topquantityid", "topquantityname", "topquantity", "toppriceid", "toppricename", "topprice", "sequence")
            .write
            .mode(SaveMode.Append)
            .jdbc("jdbc:clickhouse://192.168.45.13:8123/shtd_result", "topten", conf)
//            .write
//            .format("hudi")
//            .mode(SaveMode.Overwrite)
//            .options(getQuickstartWriteConfigs)
//            .option(SQL_ENABLE_BULK_INSERT.key,"true")
//            .option(HIVE_STYLE_PARTITIONING.key, "true")
//            .option(RECORDKEY_FIELD.key, "uuid")
//            .option(PARTITIONPATH_FIELD.key, "year,month")
//            .option(PRECOMBINE_FIELD.key, "total_count")
//            .option(TBL_NAME.key, "province_consumption_day_aggr")
//            .save(s"hdfs:///user/hive/warehouse/dws_ds_hudi.db/province_consumption_day_aggr")
    }
}
