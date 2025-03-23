package Test09
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.expressions._
import org.apache.spark.sql.functions._

import java.util.Properties
object JS03 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = Utile.GetSpark
        val dim_customer_inf = spark.table("dwd.dim_customer_inf")
        val dim_product_inf = spark.table("dwd.dim_product_info")
        val fact_order_master = spark.table("dwd.fact_order_master")
        val fact_order_detail = spark.table("dwd.fact_order_detail")
        val city_consumption_day_aggr = spark.table("dws.city_consumption_day_aggr")
        val user_consumption_day_aggr = spark.table("dws.user_consumption_day_aggr")

        val conf = new Properties()
        conf.put("user","default")
        conf.put("password", "123456")
        val udf1 = udf((d1 : Double, d2 : Double) => if(d1 > d2) "高" else if(d1 < d2) "低" else "相同")
        val win1 = Window.partitionBy("province_name", "city_name")
        val win2 = Window.partitionBy("province_name")
        city_consumption_day_aggr
            .withColumn("cityavgconsumptionTemp", count("*").over(win1)) //TODO 城市订单数量
            .withColumn("cityavgconsumptionMoney", sum("total_amount").over(win1)) //TODO 城市订单金额
            .withColumn("cityavgconsumption", col("cityavgconsumptionMoney") / col("cityavgconsumptionTemp"))
            .drop("cityavgconsumptionTemp", "cityavgconsumptionMoney")
            .withColumn("provinceavgconsumptionTemp", count("*").over(win2)) //TODO 省订单数量
            .withColumn("provinceavgconsumptionMoney", sum("total_amount").over(win2)) //TODO 省订单金额
            .withColumn("provinceavgconsumption", col("provinceavgconsumptionMoney") / col("provinceavgconsumptionTemp"))
            .drop("provinceavgconsumptionTemp", "provinceavgconsumptionMoney")
            .select(col("city_name") as "cityname", col("cityavgconsumption"), col("province_name") as "provincename", col("provinceavgconsumption"))
            .distinct()
            .withColumn("comparison", udf1(col("cityavgconsumption"), col("provinceavgconsumption")))
            .write
            .mode(SaveMode.Append)
            .jdbc("jdbc:clickhouse://192.168.45.20:8123/shtd_result?useSSL=false", "cityavgcmpprovince", conf)




    }
}
