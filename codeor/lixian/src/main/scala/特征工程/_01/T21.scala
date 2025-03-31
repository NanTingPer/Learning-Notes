package 特征工程._01

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

import java.util.Properties

object T21 {
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
        val order_user_id = order_tab
            .select("user_id","sku_id")
            .distinct()
        val order_user = order_tab
            .select("user_id")
            .distinct()
            .orderBy("user_id")
            .collect()
            .map(f => f.getLong(0))
            .zipWithIndex
            .toMap
        val order_sku = order_tab
            .select("sku_id")
            .distinct()
            .orderBy("sku_id")
            .collect()
            .map(f => f.getLong(0))
            .zipWithIndex
            .toMap
        val bro_user = spark.sparkContext.broadcast(order_user)
        val bro_sku = spark.sparkContext.broadcast(order_sku)
        val udf1 = udf((t1 : Long)=>bro_user.value.get(t1))
        val udf2 = udf((t1 : Long)=>bro_sku.value.get(t1))
        order_user_id
            .withColumn("user_id",udf1(col("user_id")))
            .withColumn("sku_id",udf2(col("sku_id")))
            .select("user_id","sku_id")
            .orderBy("user_id")

        var end_tab = order_user_id
        val sku_id = order_user_id.select("sku_id").distinct().orderBy("sku_id").collect().map(f=>f.get(0))
        val udf3 = udf((t1:Long , t2:Long)=>if (t1==t2)"1"else "0")
        sku_id.foreach(f=>
            end_tab = end_tab.withColumn(s"sku_id${f}",udf3(col("sku_id"),lit(f)))
        )
        end_tab = end_tab.orderBy("user_id").select("user_id").distinct()
        val row = end_tab
            .drop("sku_id")
            .first()
        println()
        for(i <- 0 to 9){
            print(row.get(i)+",")
        }
        println()
        println()
        end_tab
            .drop("sku_id").show()
    }
}
