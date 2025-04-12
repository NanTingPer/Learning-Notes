package _2025Two.JS


object JS3 {
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
        val order_detail = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_detail")

        val order = order_info.where(year(col("create_time")) === 2020)

        //province
        val province_m = order
            .groupBy("province_id")
            .agg(expr("percentile(final_total_amount, 0.5)") as "provincemedian")
            .withColumn("provincemedian", col("provincemedian").cast(DataTypes.DoubleType))

        //region
        val region_m = order
            .join(province, province("id") === col("province_id"))
            .join(region, region("id") === col("region_id"))
            .groupBy("region_id")
            .agg(expr("percentile(final_total_amount, 0.5)") as "regionmedian")
            .withColumn("regionmedian", col("regionmedian").cast(DataTypes.DoubleType))


        val conf = new Properties()
        conf.put("user","default")
        conf.put("password", "123456")
        province_m
            .join(province, province("id") === col("province_id"))
            .join(region, region("id") === col("region_id"))
            .join(region_m, Seq("region_id"))
            .select("province_id", "name", "region_id", "region_name", "provincemedian", "regionmedian")
            .withColumnRenamed("province_id", "provinceid")
            .withColumnRenamed("name", "provincename")
            .withColumnRenamed("region_id", "regionid")
            .withColumnRenamed("region_name", "regionname")
            .write
            .mode(SaveMode.Append)
            .jdbc("jdbc:clickhouse://192.168.45.13:8123/shtd_result", "nationmedian", conf)
    }
}
