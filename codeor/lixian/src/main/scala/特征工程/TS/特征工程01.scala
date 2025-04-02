package 特征工程.TS

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._


import java.util.Properties

object 特征工程01 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
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
        val sku_info = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false","sku_info",conf)
            .select("id","category3_id")
        val order_tab = order_detail
            .join(order_info,order_detail("order_id")===order_info("id"))
            .select(order_detail("sku_id"),order_info("user_id"))

        val all_one = sku_info
            .join(order_tab,sku_info("id")===order_tab("sku_id"))
            .select("user_id","category3_id")
            .distinct()

        val tab_6708 = all_one
            .where(col("user_id")===6708)
            .select("category3_id")
            .distinct()

        val tab_other = all_one
            .where(col("user_id")=!=6708)
        val tab_qx = tab_other
            .join(tab_6708,tab_other("category3_id")===tab_6708("category3_id"))
            .select(col("user_id"),tab_other("category3_id"))
            .distinct()
            .groupBy("user_id")
            .agg(count("*") as "count")
            .orderBy(col("count").desc,col("user_id"))
            .select("user_id")
            .limit(10)
            .collect()
            .map(f => f.get(0))
            .mkString(",")
        println("-------------------相同种类前10的id结果展示为：--------------------")
        println(tab_qx)
        println(    )

    }
}
