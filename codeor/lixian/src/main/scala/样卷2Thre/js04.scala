package yg2Thre

import org.apache.spark.sql.SparkSession

import java.util.Properties

object js04 {
    def main(args: Array[String]): Unit = {
        import org.apache.spark.sql.{SaveMode, SparkSession}
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.expressions._
        import java.util.Properties

        val spark = SparkSession.builder()
            .enableHiveSupport()
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
            .appName("awfawfawf")
            .master("local[*]")
            .getOrCreate()

        val win1 = Window.partitionBy("province_id")
        val win2 = Window.partitionBy("region_id")
        val udf111 = udf((d1 : Double, d2 : Double) => if(d1 > d2) "高" else if(d1 < d2) "低" else "相同")

        val conf = new Properties()
        conf.put("user","default")
//        conf.put("password","123456")
        val table = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dws_ds_hudi.db/province_consumption_day_aggr")
            .where(col("year") === 2020)
            .where(col("month") === 4)
            .withColumn("provinceavgconsumption", sum("total_amount").over(win1) / sum("total_count").over(win1))
            .withColumn("regionavgconsumption", sum("total_amount").over(win2) / sum("total_count").over(win2))
            .select("province_id","province_name","provinceavgconsumption","region_id","region_name","regionavgconsumption")
            .withColumnRenamed("province_id","provinceid")
            .withColumnRenamed("province_name","provincename")
            .withColumnRenamed("region_id","regionid")
            .withColumnRenamed("region_name","regionname")
            .withColumn("comparison", udf111(col("provinceavgconsumption"), col("regionavgconsumption")))
            .write
            .mode(SaveMode.Append)
            .jdbc("jdbc:clickhouse://192.168.45.10:8123/shtd_result", "provinceavgcmpregion", conf)

    }
}
