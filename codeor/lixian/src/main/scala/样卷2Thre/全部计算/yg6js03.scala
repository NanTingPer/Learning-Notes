package 样卷2Thre.全部计算

import org.apache.hudi.DataSourceWriteOptions._
import org.apache.hudi.QuickstartUtils.getQuickstartWriteConfigs
import org.apache.hudi.config.HoodieWriteConfig.TBL_NAME
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.types.DataTypes



object yg6js03 {
    def main(args: Array[String]): Unit = {
        import org.apache.spark.sql.SparkSession
        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.functions._
        import java.util.Properties
        val spark = SparkSession.builder().enableHiveSupport().appName("wfawfawf")
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
            .getOrCreate()

        val order_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_info")
        val order_detail = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_detail")
        val province = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_province").where(col("etl_date") === "20250326")
        val region = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_region").where(col("etl_date") === "20250326")


        val table = order_detail
            .where(year(col("create_time")) === "2020")
            .select("sku_id", "sku_name", "sku_num", "order_price") //商品id, 商品名称, 商品数量, 商品价格


        val win1 = Window.orderBy(col("topquantity").desc)
        //销售量前10
        val xsl10 = table.groupBy("sku_id", "sku_name")
            .agg(sum("sku_num") as "topquantity")
            .withColumn("sequence", row_number().over(win1))
            .where(col("sequence") <= 10)
            .withColumnRenamed("sku_id","topquantityid")
            .withColumnRenamed("sku_name","topquantityname")

        val win2 = Window.orderBy(col("topprice").desc)
        //销售额前10
        val xse10 = table.groupBy("sku_id", "sku_name","order_price")
            .agg((sum("sku_num") * col("order_price")).as("topprice"))
            .withColumn("sequence", row_number().over(win2))
            .where(col("sequence") <= 10)
            .withColumnRenamed("sku_id","toppriceid")
            .withColumnRenamed("sku_name","toppricename")

        val conf = new Properties()
        conf.put("user","default")
        xsl10.join(xse10, "sequence")
            .select(
                col("topquantityid").cast(DataTypes.IntegerType),
                col("topquantityname").cast(DataTypes.StringType),
                col("topquantity").cast(DataTypes.IntegerType),
                col("toppriceid").cast(DataTypes.StringType),
                col("toppricename").cast(DataTypes.StringType),
                col("topprice").cast(DataTypes.createDecimalType(20,2)),
                xsl10("sequence").cast(DataTypes.IntegerType)
            )

//
            .write
            .mode(SaveMode.Append)
            .jdbc("jdbc:clickhouse://192.168.45.10:8123/shtd_result", "topten", conf)

//            .write
//            .format("hudi")
//            .options(getQuickstartWriteConfigs)
//            .option(PARTITIONPATH_FIELD.key, "year,month")
//            .option(RECORDKEY_FIELD.key, "uuid")
//            .option(PRECOMBINE_FIELD.key, "total_count")
//            .option(HIVE_STYLE_PARTITIONING.key, "true")
//            .option(SQL_ENABLE_BULK_INSERT.key, "true")
//            .option(TBL_NAME.key, "province_consumption_day_aggr")
//            .mode(SaveMode.Overwrite)
//            .save("hdfs:///user/hive/warehouse/dws_ds_hudi.db/province_consumption_day_aggr")



    }
}
