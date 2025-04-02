package 特征工程.TS

import org.apache.spark.sql.SparkSession
import java.util.Properties

object 推荐系统01 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession.builder().enableHiveSupport().master("local[*]").appName("awf").getOrCreate()
        recommendTask(spark)
    }

    def recommendTask(spark: SparkSession): Unit = {
//        三表连接，获取用户所购买商品信息
//        val order_info = spark.table("ds_dwd.fact_order_info")
//        val order_detail = spark.table("ds_dwd.fact_order_detail")
//        val sku_info = spark.table("ds_dwd.dim_sku_info")

        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        val order_info = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false","order_info",conf)
        val order_detail = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false","order_detail",conf)
        val sku_info = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false","sku_info",conf)


        import spark.implicits._

        // 提取用户-商品信息
        val joined = order_info
            .join(order_detail, order_info.col("id") === order_detail.col("order_id"))
            .join(sku_info, order_detail.col("sku_id") === sku_info.col("id"))
            .select($"user_id", $"sku_id", order_detail.col("sku_name"))

        // 获取1605用户已购买的商品
        val sku_1605 = joined.where($"user_id" === 1605).select("sku_id").distinct

        // 获取相似top10用户已购买的商品（排除1605购买过的）
        val top10_user_id = Array(5, 10, 18, 20, 22, 23, 29, 30, 39, 44)
        val top10_sku = joined.where($"user_id".isin(top10_user_id: _*)).select("sku_id").distinct
        val top10_except_1605 = top10_sku.except(sku_1605)

        // 加载商品信息并获取待推荐商品
        val sku_info_cleaned = spark.table("dwd.sku_info_cleaned")
        val all_sku = sku_info_cleaned.select("id").distinct
        val retain_sku = all_sku.except(top10_except_1605).except(sku_1605)

        // 获取top10商品的价格和重量特征（收集到Driver端）
        val top10Features = sku_info_cleaned
            .join(top10_except_1605, sku_info_cleaned("id") === top10_except_1605("sku_id"))
            .select("price", "weight")
            .as[(Double, Double)]
            .collect()

        // 获取待推荐商品的特征
        val retainSkus = sku_info_cleaned
            .join(retain_sku, "id")
            .select("id", "price", "weight")
            .as[(Double, Double, Double)]
            .collect()

        // 计算余弦相似度的函数
        def cosineSimilarity(aPrice: Double, aWeight: Double, bPrice: Double, bWeight: Double): Double = {
            val dotProduct = aPrice * bPrice + aWeight * bWeight
            val normA = math.sqrt(aPrice * aPrice + aWeight * aWeight)
            val normB = math.sqrt(bPrice * bPrice + bWeight * bWeight)
            if (normA == 0 || normB == 0) 0.0 else dotProduct / (normA * normB)
        }

        // 计算每个保留商品与top10的平均相似度
        val recommendations = retainSkus.map { case (id, price, weight) =>
                val avgSim = if (top10Features.nonEmpty) {
                    top10Features.map { case (p, w) =>
                        cosineSimilarity(price, weight, p, w)
                    }.sum / top10Features.length
                } else 0.0
                (id, avgSim)
            }.sortBy(-_._2) // 降序排序
            .take(5)       // 取前5

        // 打印结果
        recommendations.zipWithIndex.foreach { case ((id, sim), idx) =>
            println(f"相似度top${idx + 1}%d(商品id：$id，平均相似度：$sim%.2f)")
        }
    }
}
