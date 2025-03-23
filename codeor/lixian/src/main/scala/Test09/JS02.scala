package Test09

import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions._

object JS02 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = Utile.GetSpark
        val dim_customer_inf = spark.table("dwd.dim_customer_inf")
        val dim_product_inf = spark.table("dwd.dim_product_info")
        val fact_order_master = spark.table("dwd.fact_order_master")
        val fact_order_detail = spark.table("dwd.fact_order_detail")

        val 有效单 = fact_order_master.where(col("order_status") === "已下单")
        val oneData = 有效单
            .withColumn("year", substring(col("create_time"), 1, 4))
            .withColumn("month", substring(col("create_time"), 5, 2))
            .groupBy(col("province"), col("city"), col("year"), col("month"))
            .agg(count(col("order_id")) as "total_count", sum("order_money") as "total_amount")

        val win1 = Window.partitionBy("province", "year", "month").orderBy("total_amount")
        oneData.withColumn("sequence", row_number().over(win1))
            .select(
                col("city") as "city_name",
                col("province") as "province_name",
                col("total_amount"),
                col("total_count"),
                col("sequence"),
                col("year"),
                col("month"))
            .write
            .mode(SaveMode.Overwrite)
            .format("hive")
            .saveAsTable("dws.city_consumption_day_aggr")

        spark.sql("select * from dws.city_consumption_day_aggr order by total_amount desc, total_count desc limit 5")
            .show()


    }
}
