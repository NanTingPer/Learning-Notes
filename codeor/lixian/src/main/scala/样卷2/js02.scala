package yg2


import java.net.URI

object js02 {
    def main(args: Array[String]): Unit = {
        import org.apache.spark.sql._
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.expressions._
        import org.apache.hudi.DataSourceWriteOptions._
        import org.apache.hudi.QuickstartUtils._
        import org.apache.hudi.config.HoodieWriteConfig.TBL_NAME
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession.builder()
            .master("yarn")
            .enableHiveSupport()
            .config("spark.sql.extension", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .master("local[*]")
            .appName("dawfawfawfawfawf")
            .getOrCreate()

        val province = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_province").drop("create_time")
        val region = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_region").drop("create_time")

        val order_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_info")

        order_info.show
        val filetrOrder_info = order_info.select("final_total_amount", "create_time", "province_id")
        filetrOrder_info.show
        val jointable = filetrOrder_info.join(province, province("id") === filetrOrder_info("province_id"))
            .select("final_total_amount", "create_time", "province_id", "name", "region_id")
            .withColumnRenamed("name", "province_name")
            .join(region, col("region_id") === region("id"))
            .withColumn("year", year(col("create_time")))
            .withColumn("month", month(col("create_time")))
        jointable.show()
        val win1 = Window.partitionBy("province_id", "year", "month") //按照城市的年月计算订单数量和金额
        val win2 = Window.partitionBy("province_id", "year", "month").orderBy(col("total_amount").desc)//取出一条

        val win3 = Window.partitionBy("region_id", "year", "month").orderBy(col("total_amount").desc)//按照地区分组，计算排名
        var aggtable = jointable.withColumn("total_count", count("*").over(win1))
        aggtable = aggtable.withColumn("total_amount", sum("final_total_amount").over(win1))
        aggtable =  aggtable.withColumn("temp", row_number().over(win2))
        aggtable = aggtable.where(col("temp") === 1)
        aggtable = aggtable.drop("temp")
        aggtable = aggtable.withColumn("uuid", expr("uuid()"))
        aggtable.select("*").show

        val fin = aggtable
            .withColumn("sequence", row_number().over(win3))
            .select("uuid", "province_id", "province_name", "region_id", "region_name", "total_amount", "total_count", "sequence", "year", "month")

        fin.show

        fin
            .write
            .format("hudi")
            .options(getQuickstartWriteConfigs)
            .option(PARTITIONPATH_FIELD.key, "year")
            .option(PARTITIONPATH_FIELD.key, "month")
            .option(RECORDKEY_FIELD.key, "uuid")
            .option(PRECOMBINE_FIELD.key, "total_count")
            .option(HIVE_STYLE_PARTITIONING.key, "true")
            .option(SQL_ENABLE_BULK_INSERT.key, "true")
            .option(TBL_NAME.key, "province_consumption_day_aggr")
            .mode(SaveMode.Overwrite)
            .save("hdfs:///user/hive/warehouse/dws_ds_hudi.db/province_consumption_day_aggr")
    }
}
