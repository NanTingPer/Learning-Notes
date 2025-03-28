package 样卷2Thre.全部计算
object hiveYjJs03 {
  def main(args: Array[String]): Unit = {
    import org.apache.spark.sql.{SaveMode, SparkSession}
    import org.apache.spark.sql.expressions.Window
    import org.apache.spark.sql.functions._
    import java.util.Properties

      System.setProperty("HADOOP_USER_NAME","root")
    val spark = SparkSession.builder()
      .appName("qwe")
      .master("local[*]")
      .config("hive.exec.dynamic.partition","true")
//      .config("spark.serializer","org.apache.spark.serializer.KryoSerializer")
//      .config("spark.sql.extensions","org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
      .enableHiveSupport()
      .getOrCreate()


    val province_aggr = spark.table("dws.province_consumption_day_aggr")
    val avg_province = province_aggr.groupBy("province_id")
      .agg(sum("total_amount") as "total_amount", count("total_count") as "total_count")
      .withColumn("provinceavgconsumption", col("total_amount") / col("total_count"))
      .select("province_id", "provinceavgconsumption")
    val avg_region = province_aggr.groupBy("region_id")
      .agg(sum("total_amount") as "total_amount", count("total_count") as "total_count")
      .withColumn("regionavgconsumption", col("total_amount") / col("total_count"))
      .select("region_id", "regionavgconsumption")

//
    province_aggr.show()
    avg_province.show()
    val total_table = province_aggr
      .join(avg_province,Seq("province_id"))
      .select(
        province_aggr("province_id"),
        province_aggr("province_name"),
        avg_province("provinceavgconsumption"),
        province_aggr ("region_id"),
        province_aggr("region_name")
      )
    val total_table1 = total_table
      .join(avg_region,Seq("region_id"))
      .select(
        total_table("province_id"),
        total_table("province_name"),
        total_table("provinceavgconsumption"),
        total_table("region_id"),
        total_table("region_name"),
        avg_region("regionavgconsumption")
      )
    val udf1 = udf((d1 : Double, d2 : Double) => if(d1 > d2) "高" else if(d1 < d2) "低" else "相同")
    val end_table = total_table1
      .withColumn("comparison",udf1(col("provinceavgconsumption"),col("regionavgconsumption")))
//    end_table.show()
    val conf = new Properties()
    conf.put("user","root")
    conf.put("password","123456")
    end_table.
      write.
      format("jdbc").
      mode(SaveMode.Overwrite).
      jdbc("jdbc:mysql://192.168.45.13:3306/shtd_result?useSSL=false","provinceavgcmpregion",conf)
  }
}
