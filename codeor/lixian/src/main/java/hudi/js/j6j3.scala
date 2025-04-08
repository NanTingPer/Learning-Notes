package hudi.js

// --conf "spark.sql.extensions=org.apache.spark.sql.hudi.HoodieSparkSessionExtension" \
// --conf "spark.serializer=org.apache.spark.serializer.KryoSerializer"
object j6j3 {
    def main(args: Array[String]): Unit = {
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.types.DataTypes
        import org.apache.spark.sql.{SaveMode, SparkSession}

        import java.util.Properties
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .master("local[*]")
            .getOrCreate()

        val region = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_region").where(col("etl_date") === "20250407")
        val province = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_province").where(col("etl_date") === "20250407")
        val user_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_user_info").where(col("etl_date") === "20250407")
        val order_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_info")
        val order_detail = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_detail")

        val order_data = order_detail
            .where(year(col("create_time")) === 2020)

        //销量top10
        val win1 = Window.orderBy(col("topquantity").desc)
        val topquantity = order_data
            .groupBy("sku_id", "sku_name")
            .agg(count("*") as "topquantity")
            .withColumn("sequence", row_number().over(win1))
            .withColumnRenamed("sku_name", "topquantityname")
            .withColumnRenamed("sku_id", "topquantityid")
            .limit(10)

        //销售额top10
        val topprice = order_data
            .withColumn("xiaoshoue", col("sku_num") * col("order_price"))
            .groupBy("sku_id", "sku_name")
            .agg(sum("xiaoshoue") as "topprice")
            .withColumn("sequence", row_number().over(Window.orderBy(col("topprice").desc)))
            .withColumnRenamed("sku_name", "toppricename")
            .withColumnRenamed("sku_id", "toppriceid")
            .limit(10)

        val conf = new Properties()
        conf.put("user","default")
        conf.put("password","123456")
        topquantity.join(topprice, Seq("sequence"))
            .select("topquantityid", "topquantityname", "topquantity", "toppriceid", "toppricename", "topprice", "sequence")
            .write
            .mode(SaveMode.Append)
            .jdbc("jdbc:clickhouse://192.168.45.13:8123/shtd_result", "topten", conf)

//        fin
//            .write
//            .mode(SaveMode.Append)
//            .format("hudi")
//            .options(getQuickstartWriteConfigs)
//            .option(SQL_ENABLE_BULK_INSERT.key(), "true")
//            .option(HIVE_STYLE_PARTITIONING.key(), "true")
//            .option(RECORDKEY_FIELD.key(), "uuid")
//            .option(PARTITIONPATH_FIELD.key(), "year, month")
//            .option(PRECOMBINE_FIELD.key(), "total_count")
//            .option(TBL_NAME.key(), "province_consumption_day_aggr")
//            .save("hdfs:///user/hive/warehouse/dws_ds_hudi.db/province_consumption_day_aggr")
    }
}
