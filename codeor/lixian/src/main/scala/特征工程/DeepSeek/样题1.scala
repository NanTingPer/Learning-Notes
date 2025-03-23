package 特征工程.DeepSeek

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

import java.util.Properties

object 样题1 {
    def main(args: Array[String]): Unit = {
        val spark = SparkSession.builder()
            .appName("UserSkuMapping")
            .master("local[*]")
            .enableHiveSupport()
            .getOrCreate()

        import spark.implicits._
        val conf = new Properties()
        conf.put("user","root")
        conf.put("password", "123456")
        // 读取MySQL表数据
        val orderDetail = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "order_detail", conf)
        val sku_info = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "sku_info", conf)
        val orderInfo = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "order_info", conf)


        // 关联订单表获取用户-商品关系并去重
        val userSkuDF = orderInfo.join(orderDetail, orderInfo("id") === orderDetail("order_id"))
            .select("user_id", "sku_id")
            .distinct()

        // 获取目标用户购买的商品集合
        val targetSkus = userSkuDF.filter($"user_id" === 6708)
            .select("sku_id")
            .distinct()

        // 计算其他用户与目标用户的共同商品数
        val resultDF = userSkuDF.filter($"user_id" =!= 6708)
            .join(targetSkus, Seq("sku_id")) // 内连接过滤出共同商品
            .groupBy("user_id")
            .agg(countDistinct("sku_id").alias("common_count"))
            .orderBy($"common_count".desc, $"user_id".asc)
            .limit(10)

//         提取结果并格式化输出
        val userIds = resultDF.select("user_id")
            .as[Long]
            .collect()
            .map(_.toString)
            .mkString(",")

        println("-------------------相同种类前 10 的 id 结果展示为----------------")
        println(userIds)

        spark.stop()
    }
}