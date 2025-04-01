
object ERRO04 {
    def main(args: Array[String]): Unit = {
        import org.apache.spark.sql.SaveMode
        import org.apache.spark.sql.SparkSession
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.expressions._
        import java.util.Properties
        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .appName("hive")
            .getOrCreate()
        //todo 读取要求表 并更改字段名称
        //todo province_consumption_day_aggr表计算的是省份金额
        val table = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dws_ds_hudi.db/province_consumption_day_aggr")
            .where(col("year") === 2020)
            .where(col("month") === 4)
            .withColumnRenamed("province_id","provinceid")
            .withColumnRenamed("province_name","provincename")
            .withColumnRenamed("region_id","regionid")
            .withColumnRenamed("region_name","regionname")

        //todo 计算地区平均订单金额 地区总金额 / 地区订单总数
        val region_avg = table
            .withColumnRenamed("regionid", "tworegionid")
            .withColumnRenamed("regionname","tworegionname")
            .groupBy("tworegionid", "tworegionname")
            .agg(sum("total_count") as "order_count", sum("total_amount") as "order_amount")
            .withColumn("regionavgconsumption", col("order_amount") / col("order_count"))
        region_avg.show()

        //todo 计算省份平均订单金额 省份总金额 / 省份订单总数,并结合地区平均订单金额
        val fin = table
            .withColumn("provinceavgconsumption", col("total_amount") / col("total_count"))
            .join(region_avg, region_avg("tworegionid") === table("regionid"))
            .select(
                table("provinceid"),
                table("provincename"),
                col("provinceavgconsumption"),
                table("regionid"),
                region_avg("regionavgconsumption"))

        //todo 计算排名
        val myudf = udf((d1 : Double, d2 : Double) => if(d1 > d2)"高" else if(d1 < d2) "低" else "相同")
        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        fin
            .withColumn("comparison", myudf(col("provinceavgconsumption"), col("regionavgconsumption")))
            .write
            .mode(SaveMode.Overwrite)
            .jdbc("jdbc:mysql://192.168.45.13:3306/shtd_result?useSSL=false", "provinceavgcmpregion", conf)
    }
}
