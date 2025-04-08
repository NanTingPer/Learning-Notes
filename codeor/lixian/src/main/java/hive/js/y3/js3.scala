package hive.js.y3

import java.util.Properties

object js3 {
    def main(args: Array[String]): Unit = {
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.expressions._
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

        //total_count  当月订单数量
        //total_amount 当月订单总金额

        val win1 = Window.partitionBy("region_id").orderBy(col("provinceamount").desc)
        //每个省份当年前3
        val province3 = dws
            .groupBy("province_id", "region_id", "region_name", "province_name","year")
            .agg(sum("total_amount") as "provinceamount")
            .withColumn("provinceamount", round(col("provinceamount"), 0).cast(DataTypes.IntegerType))
            .withColumn("pm", row_number().over(win1))
            .where(col("pm") <= 3)
            .orderBy("region_id")

        val bigTable = province3
            .withColumn("province1", lag(col("province_id"), 1).over(win1))
            .withColumn("province2", lag(col("province_id"), 2).over(win1))
            .withColumn("province_name1", lag(col("province_name"), 1).over(win1))
            .withColumn("province_name2", lag(col("province_name"), 2).over(win1))
            .withColumn("province_amount1", lag(col("provinceamount"), 1).over(win1))
            .withColumn("province_amount2", lag(col("provinceamount"), 2).over(win1))
            .where(col("province2").cast(DataTypes.StringType) =!= "null")

//        province2最大
        val fin = bigTable
            .withColumn("provinceids", concat(col("province2"), lit(","), col("province1"), lit(","), col("province_id")))
            .withColumn("provomcenames", concat(col("province_name2"), lit(","), col("province_name1"), lit(","), col("province_name")))
            .withColumn("provinceamount", concat(col("province_amount2"), lit(","), col("province_amount1"), lit(","), col("provinceamount")))
            .select("region_id", "region_name", "provinceids", "provomcenames", "provinceamount")
            .withColumnRenamed("region_id", "regionid")
            .withColumnRenamed("region_name", "regionname")

        val conf = new Properties()
        conf.put("user", "root")
        conf.put("password", "123456")
        fin
            .write
            .mode(SaveMode.Overwrite)
            .jdbc("jdbc:mysql://192.168.45.13:3306/shtd_result?useSSL=false", "regiontopthree", conf)

  }
}
