package yg2

import org.apache.spark.sql.SaveMode
object js03 {
    def main(args: Array[String]): Unit = {
        import org.apache.spark.sql.SparkSession
        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.functions._
        import java.util.Properties

        val spark = SparkSession.builder()
            .master("yarn")
            .enableHiveSupport()
            .config("spark.sql.extension", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .master("local[*]")
            .appName("dawfawfawfawfawf")
            .getOrCreate()

        val province_consumption_day_aggr = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dws_ds_hudi.db/province_consumption_day_aggr")


        val udf1 = udf((d1 : Double, d2 : Double) => if(d1 > d2) "高" else if(d1 < d2) "低" else "相同")
        val win1 = Window.partitionBy("province_id")
        val win2 = Window.partitionBy("region_id")
        val fin = province_consumption_day_aggr
            .select("province_id", "province_name", "region_id", "region_name", "total_amount", "total_count")
            .withColumn("provinceavgconsumption", sum("total_amount").over(win1) / sum("total_count").over(win1))
            .withColumn("regionavgconsumption", sum("total_amount").over(win2) / sum("total_count").over(win2))
            .withColumn("comparison", udf1(col("provinceavgconsumption"), col("regionavgconsumption")))
            .withColumnRenamed("province_id", "provinceid")
            .withColumnRenamed("province_name", "provincename")
            .withColumnRenamed("region_id", "regionid")
            .withColumnRenamed("region_name", "regionname")
            .withColumnRenamed("region_name", "regionname")
            .select("provinceid","provincename","provinceavgconsumption","regionid","regionname","regionavgconsumption","comparison")

        fin.show()

        val conf = new Properties()
        conf.put("user","default")
        conf.put("password","123456")
        fin.write.mode(SaveMode.Append).jdbc("jdbc:clickhouse://192.168.45.13:8123/shtd_result", "provinceavgcmpregion", conf)
    }

}
