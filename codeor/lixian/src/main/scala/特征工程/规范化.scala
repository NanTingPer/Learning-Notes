package 特征工程

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.col

import java.util.Properties

object 规范化 {
    def main(args: Array[String]): Unit = {
        //MySQL中shtd_store中相关商品表（sku_info），
        // 获取id、spu_id、price、weight、tm_id、category3_id 这六个字段并进行数据预处理，
        // 对price、weight进行规范化(StandardScaler)处理
        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .master("local[*]")
            .appName("standard")
            .getOrCreate()

        val conf = new Properties();conf.put("user","root");conf.put("password","123456")
        val table = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "sku_info", conf)
            .select("id","price", "weight")

        val summaryTable = table.summary()

        val price = summaryTable.select("price")
        val priceStddev = price.where(col("summary") === "stddev").first()(0)
        val priceMean = price.where(col("summary") === "mean").first()(0)

        //Stddev => 标准差(方差)
        //Mean => 均值
        val weight = summaryTable.select("weight")
        val weightStddev = weight.where(col("summary") === "stddev").first()(0)
        val weightMean = weight.where(col("summary") === "mean").first()(0)

        //z-score标准化公式 = (value - mean_value)/stddev_value
        //说明 : z = (x - u) / s
        //      => 通过去除均值和缩放到单位方差来标准化特征
        // x是原值，u是均值，s是标准差
        table
            .withColumn("price", (col("price") - priceMean) / priceStddev)
            .withColumn("weight", (col("weight") - weightMean) / weightStddev)
            .orderBy("id")
            .show

    }
}
