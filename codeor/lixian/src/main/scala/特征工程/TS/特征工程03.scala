package 特征工程.TS

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

import java.util.Properties

object 特征工程03 {
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
        val order_tab = order_info.join(order_detail,order_info("id")===order_detail("order_id"))
            .select("sku_id","user_id")
            .distinct()
        val start_tab = sku_info
            .join(order_tab,order_tab("sku_id")===sku_info("id"))
            .select("user_id","sku_id")
            .distinct()
        val user_id = start_tab
            .select("user_id")
            .distinct()
            .collect()
            .map(f=>f.getLong(0))
            .zipWithIndex
            .toMap

        val sku_id = start_tab
            .select("sku_id")
            .distinct()
            .collect()
            .map(f=>f.getLong(0))
            .zipWithIndex
            .toMap
        val udf1 = udf((t1:Long)=> user_id.get(t1))
        val udf2 = udf((t1:Long)=> sku_id.get(t1))

        val end = start_tab

            .select("user_id", "sku_id")
            .withColumn("user_id", udf1(col("user_id")))
            .withColumn("sku_id", udf2(col("sku_id")))
            .orderBy("user_id", "sku_id")
            .limit(5)
            .collect()
        println("-------user_id_mapping与sku_id_mapping数据前5条如下：-------")
        end.foreach(f => println(s"${f.get(0)}:${f.get(1)}"))
    }
}
