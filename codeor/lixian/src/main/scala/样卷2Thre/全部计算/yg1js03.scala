package yg2Thre.alljs

object yg1js03 {
    def main(args: Array[String]): Unit = {
        import org.apache.spark.sql.SparkSession
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.expressions._
        import java.util.Properties
        import org.apache.spark.sql.SaveMode
        val spark = SparkSession.builder()
            .enableHiveSupport()
            .appName("wfawfawf")
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
            .getOrCreate()

        val conf: Properties = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        val url = "jdbc:mysql://192.168.45.13:3306/shtd_result?useSSL=false"
        val order_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_info")
        val province = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_province").where(col("etl_date") === "20250326")
        val region = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_region").where(col("etl_date") === "20250326")
        val provinceWin = Window.partitionBy("province_id")
        val twin = Window.partitionBy("province_id").orderBy("provinceavgconsumption")

        val udf1 = udf((d1 : Double, d2 : Double) => if(d1 > d2) "高" else if (d1 < d2) "低" else "相同")
        val AllprovinceWin = Window.partitionBy()
        val frame = order_info
            .where(year(col("create_time")) === 2020)
            .where(month(col("create_time")) === 4)
            .withColumn("allprovinceavgconsumption", sum("final_total_amount").over(AllprovinceWin) / count("*").over(AllprovinceWin))
            //省平均订单
            .withColumn("provinceavgconsumption", sum("final_total_amount").over(provinceWin) / count("*").over(provinceWin))
            .withColumn("temp", row_number().over(twin))
            .where(col("temp") === 1)
            .withColumn("comparison", udf1(col("provinceavgconsumption"), col("allprovinceavgconsumption")))
            .join(province, col("province_id") === province("id"))
            .select("province_id", "name", "provinceavgconsumption", "allprovinceavgconsumption", "comparison")
            .withColumnRenamed("province_id", "provinceid")
            .withColumnRenamed("name", "provincename")
        frame.show
        frame
            .write
            .mode(SaveMode.Overwrite)
            .jdbc(url, "provinceavgcmp", conf)

    }
}
