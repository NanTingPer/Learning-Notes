package hive.js.y3

import java.util.Properties

object js2 {
    def main(args: Array[String]): Unit = {
        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.types.DataTypes
        import org.apache.spark.sql.{SaveMode, SparkSession}
        val spark = SparkSession
            .builder()
            .config("hive.exec.dynamic.partition.mode","nonstrict")
            .master("local[*]")
            .appName("hive")
            .enableHiveSupport()
            .getOrCreate()

        val region = spark.table("dwd.dim_region").where(col("etl_date") === "20250407")
        val province = spark.table("dwd.dim_province").where(col("etl_date") === "20250407")
        val order_info = spark.table("dwd.fact_order_info")

        val dws = spark.table("dws.province_consumption_day_aggr")
        val yxdws = dws
            .where(col("year") === 2020)
            .where(col("month") === 4)

        //total_count  当月订单数量
        //total_amount 当月订单总金额

        //avg省 订单金额
        val province_avg = yxdws
            .withColumn("provinceavgconsumption", col("total_amount") / col("total_count"))

        //avg地区
        val region_avg = yxdws
            .groupBy("region_id")
            .agg(sum("total_count") as "sl", sum("total_amount") as "je")
            .withColumn("regionavgconsumption", col("je") / col("sl"))

        val fin = province_avg.join(region_avg, Seq("region_id"))

        val fin2 = fin
            .select("province_id", "province_name", "provinceavgconsumption", "region_id", "region_name", "regionavgconsumption")
            .withColumnRenamed("province_id", "provinceid")
            .withColumnRenamed("province_name", "provincename")
            .withColumnRenamed("region_id", "regionid")
            .withColumnRenamed("region_name", "regionname")
            .withColumn("provinceavgconsumption", col("provinceavgconsumption").cast(DataTypes.DoubleType))
            .withColumn("regionavgconsumption", col("regionavgconsumption").cast(DataTypes.DoubleType))

        val myudf = udf((d1 : Double, d2 : Double) => if(d1 > d2) "高" else if(d1 < d2) "低" else "相同")
        val conf = new Properties()
        conf.put("user", "root")
        conf.put("password", "123456")
        fin2
            .withColumn("comparison", myudf(col("provinceavgconsumption"),col("regionavgconsumption")))
            .write
            .mode(SaveMode.Overwrite)
            .jdbc("jdbc:mysql://192.168.45.13:3306/shtd_result?useSSL=false", "provinceavgcmpregion", conf)

  }
}
