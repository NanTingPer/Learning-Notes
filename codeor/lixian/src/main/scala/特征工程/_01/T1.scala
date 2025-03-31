package 特征工程._01

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql._
import java.util.Properties

object T1 {
    def main(args: Array[String]): Unit = {
        val spark = SparkSession.builder()
            .master("local[*]")
            .appName("qwe")
            .enableHiveSupport()
            .getOrCreate()

        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        val order_info = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false","order_info",conf)
        val order_detail = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false","order_detail",conf)
        val sku_info = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "sku_info", conf)
        val order_tab = order_info.join(order_detail,order_info("id")===order_detail("order_id"))
            .select(col("user_id"),col("sku_id"))
        val tab_6708 = order_tab.join(sku_info,sku_info("id")===order_tab("sku_id"))
            .where(col("user_id")===6708)
            .select("category3_id")
            .distinct()

        val tab_other = order_tab.join(sku_info, sku_info("id") === order_tab("sku_id"))
            .where(col("user_id") =!= 6708)
            .select(col("user_id"), col("category3_id"))
            .distinct()

        val tab_6708_other_to = tab_6708
            .join(tab_other,Seq("category3_id"))
            .select(tab_other("user_id"),tab_other("category3_id"))
        tab_6708_other_to.groupBy("user_id").agg(count("*") as "count")
            .where(col("count") === 4)
            .orderBy(col("user_id"))
            .select("user_id")
            .limit(10)
            .collect()
            .foreach(f => print(f.get(0)+","))
        println()
    }
}
