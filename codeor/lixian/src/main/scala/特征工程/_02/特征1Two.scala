package 特征工程._02

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

import java.util.Properties

object 特征1Two {
    def main(args: Array[String]): Unit = {
        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .master("local[*]")
            .appName("ddd")
            .getOrCreate()

        val conf = new Properties()
        conf.put("user","root")
        conf.put("password", "123456")
        val order_detail = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "order_detail", conf)
        val sku_info = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "sku_info", conf)
        val order_info = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "order_info", conf)

        order_detail.join(order_info, col("order_id") === order_info("id"))
            .select(
                order_detail("order_id"),
                order_detail("sku_id"),
                order_info("user_id")
            ).show

        

    }
}
