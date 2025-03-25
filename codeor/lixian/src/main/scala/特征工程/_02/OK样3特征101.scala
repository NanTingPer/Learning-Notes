package 特征工程._02

    import org.apache.spark.sql.SparkSession
    import org.apache.spark.sql.functions._

import java.util.Properties

object OK样3特征101 {
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
        val orderDetail = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "order_detail", conf)
        val sku_info = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "sku_info", conf)
        val orderInfo = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "order_info", conf)

        // 关联表并去重获取用户-商品对
        val userSkuDF = orderInfo.join(orderDetail, orderInfo("id") === orderDetail("order_id"))
            .select("user_id", "sku_id")
            .distinct()

        // 生成用户ID映射
        val userMapping = userSkuDF.select("user_id")
            .distinct()
            .orderBy("user_id")
            .as[Long]
            .collect()
            .zipWithIndex
            .toMap

        // 生成商品ID映射
        val skuMapping = userSkuDF.select("sku_id")
            .distinct()
            .orderBy("sku_id")
            .as[Long]
            .collect()
            .zipWithIndex
            .toMap

        // 广播映射
        val userMapBC = spark.sparkContext.broadcast(userMapping)
        val skuMapBC = spark.sparkContext.broadcast(skuMapping)

        // 定义UDF转换
        val userMappingUDF = udf((userId: Long) => userMapBC.value.getOrElse(userId, -1))
        val skuMappingUDF = udf((skuId: Long) => skuMapBC.value.getOrElse(skuId, -1))

        // 应用转换并排序
        val resultDF = userSkuDF
            .withColumn("user_mapping", userMappingUDF($"user_id"))
            .withColumn("sku_mapping", skuMappingUDF($"sku_id"))
            .orderBy($"user_id", $"sku_id")
            .select("user_mapping", "sku_mapping")
            .limit(5)

        // 输出结果
        resultDF.show()
    }
}
