
package 特征工程.TS

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.DataTypes

import java.util.Properties

object 特征工程04 {
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

        var end = start_tab

            .select("user_id", "sku_id")
            .withColumn("user_id", udf1(col("user_id")))
            .withColumn("sku_id", udf2(col("sku_id")))
            .orderBy("user_id", "sku_id")
            .limit(5)

        val sku_id1 = end.select("sku_id").distinct().orderBy("sku_id").collect().map(f=>f.get(0))
        val udf3 = udf((t1:Double,t2:Double)=> if(t1==t2) "1.0" else "0.0")
        sku_id1.foreach(f=> end = end.withColumn(s"sku_id${f}",udf3(lit(f),col("sku_id"))))
        val row = end
            .withColumn("user_id", col("user_id").cast(DataTypes.DoubleType))
            .drop("sku_id")
            .write
            .mode(SaveMode.Overwrite)
            .format("hive")
            .saveAsTable("dwd.sku_info_cleaned")
//            .limit(1)
//            .first()
//        println("---------------第一行前5列结果展示为---------------")
//        print(row.toSeq.take(5).mkString(","))
//        println()

    }
}
