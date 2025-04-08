package hudi.js

// --conf "spark.sql.extensions=org.apache.spark.sql.hudi.HoodieSparkSessionExtension" \
// --conf "spark.serializer=org.apache.spark.serializer.KryoSerializer"
object j2j4 {
    def main(args: Array[String]): Unit = {
        import org.apache.hudi.DataSourceWriteOptions._
        import java.util.Properties
        import org.apache.hudi.QuickstartUtils._
        import org.apache.hudi.config.HoodieWriteConfig._
        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.types.DataTypes
        import org.apache.spark.sql.{SaveMode, SparkSession}
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

        val order_data = order_info
            .where(year(col("create_time")) === 2020)
            .where(month(col("create_time")) === 4)

        //省份平均
        val provinceavg = order_data
            .groupBy("province_id")
            .agg(avg("final_total_amount") as "provinceavgconsumption")

        val regionavg = order_data
            .join(province, province("id") === col("province_id"))
            .join(region, region("id") === col("region_id"))
            .groupBy("region_id", "region_name")
            .agg(avg("final_total_amount") as "regionavgconsumption")

        val fin1 = provinceavg
            .join(province, province("id") === col("province_id"))
            .join(region, region("id") === col("region_id"))
            .join(regionavg, Seq("region_id"))

        val myudf = udf((d1 : Double, d2 : Double) => if(d1 > d2) "高" else if(d1 < d2) "低" else "相同")

        val fin2 = fin1
            .withColumn("comparison", myudf(col("provinceavgconsumption"), col("regionavgconsumption")))
            .select(
                provinceavg("province_id"),
                province("name"),
                col("provinceavgconsumption"),
                region("id") as "rrrrregion_id",
                col("regionavgconsumption"),
                col("comparison"))

        val conf = new Properties()
        conf.put("user","default")
        conf.put("password", "123456")

        fin2
            .join(region, region("id") === col("rrrrregion_id"))
            .select("province_id", "name", "provinceavgconsumption", "rrrrregion_id", "region_name","regionavgconsumption", "comparison")
            .withColumnRenamed("rrrrregion_id", "regionid")
            .withColumnRenamed("province_id", "provinceid")
            .withColumnRenamed("name", "provincename")
            .withColumnRenamed("region_id", "regionid")
            .withColumnRenamed("region_name", "regionname")
            .withColumn("provinceavgconsumption", col("provinceavgconsumption").cast(DataTypes.DoubleType))
            .withColumn("regionavgconsumption", col("regionavgconsumption").cast(DataTypes.DoubleType))
            .write
            .mode(SaveMode.Append)
            .jdbc("jdbc:clickhouse://192.168.45.13:8123/shtd_result", "provinceavgcmpregion", conf)

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
