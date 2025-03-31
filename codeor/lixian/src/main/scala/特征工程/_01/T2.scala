package 特征工程._01

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

import java.util.Properties

object T2 {
    def main(args: Array[String]): Unit = {
        val spark = SparkSession.builder()
            .master("local[*]")
            .appName("qwe")
            .enableHiveSupport()
            .getOrCreate()

        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
//        val order_info = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false","order_info",conf)
//        val order_detail = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false","order_detail",conf)
        val sku_info = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "sku_info", conf)
            .select("id","spu_id","price","weight","tm_id","category3_id")
        val sku = sku_info.select("price","weight").summary("stddev","mean")
        val price_stddev = sku.where(col("summary")==="stddev").first().get(1)
        val price_mean = sku.where(col("summary")==="mean").first().get(1)

        val weight_stddev = sku.where(col("summary")==="stddev").first().get(2)
        val weight_mean = sku.where(col("summary")==="mean").first().get(2)
        // z-score标准化处理。计算公式：stddev = (value - mean_value)/stddev_value
        var sku_info_new = sku_info
            .withColumn("price", (col("price")-price_mean)/price_stddev)
            .withColumn("weight", (col("weight")-weight_mean)/weight_stddev)

        //获取全部sku_id
        val spu_id = sku_info.select("spu_id").distinct().collect().map(f => f.get(0))

        //获取全部tm_id
        val tm_id = sku_info.select("tm_id").distinct().collect().map(f => f.get(0))

        //全部category3_id
        val category3_id = sku_info.select("category3_id").distinct().collect().map(f => f.get(0))

        val skuudf = udf((value1 : Long, value2 : Long) => if(value1 == value2)"1" else "0")
        //sku_id OneHit
        spu_id.foreach(f => {
            sku_info_new = sku_info_new.withColumn(s"spu_id#${f}", skuudf(col("spu_id"), lit(f)))
        })
        tm_id.foreach(f => {
            sku_info_new = sku_info_new.withColumn(s"tm_id#${f}", skuudf(col("tm_id"), lit(f)))
        })
        category3_id.foreach(f => {
            sku_info_new = sku_info_new.withColumn(s"category3_id#${f}", skuudf(col("category3_id"), lit(f)))
        })
        val row = sku_info_new
            .drop("spu_id", "tm_id", "category3_id")
            .first()
        println()
        for(i <- 0 to 9){
            print(row.get(i) + ",")
        }
        println()
    }
}
