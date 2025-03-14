//import org.apache.spark
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Row, SparkSession}

object Recommend1 {
    def main(args: Array[String]): Unit = {
        // 创建SparkSession实例
        val spark = SparkSession.builder()
            .master("local[*]")
            .appName("recommend1")
            //      .config("spark.driver.memory","4g")
            //      .config("spark.executor.memory","6g")
            .enableHiveSupport()
            .getOrCreate()

        // 执行数据挖掘部分-推荐
        recommendTask(spark)
    }

    // 推荐任务实现
    def recommendTask(spark:SparkSession): Unit = {
        /* 1. 构造users-itmes */
        // 找出维表中存在的所有用户id
        val user_ids = spark.sql(
            """
              |select distinct id as user_id
              |from gz_ds_dwd.dim_user_info
              |where etl_date="20240402"
              |""".stripMargin)

        // 订单事实表，并瘦身
        val order_info = spark
            // 加载订单事实表,只保留必须的字段
            .sql("select distinct id as order_id, user_id from gz_ds_dwd.fact_order_info")
            // 剔除维表中不存在的用户的订单
            .join(broadcast(user_ids), "user_id")

        // 找出维表中存在的所有商品id(sku_id)
        val sku_ids = spark.sql(
            """
              |select distinct id as sku_id
              |from gz_ds_dwd.dim_sku_info
              |where etl_date="20240402"
              |""".stripMargin)

        // 订单明细表，并瘦身
        val order_detail = spark
            // 加载订单明细表, 只保留必须的字段(订单id, 商品id)
            .sql("select distinct order_id, sku_id from gz_ds_dwd.fact_order_detail")
            // 剔除维表中不存在的商品id
            .join(broadcast(sku_ids), "sku_id")

        // 两表连接, 提取 用户id-商品id（全部的 user-item）
        val users_items_df = order_info
            .join(order_detail, "order_id")
            .select("user_id","sku_id")
            .distinct()
        /* 全部的 user-item
        only showing top 20 rows
         */

        /* 2. 检索出待推荐商品的id集 */
        import spark.implicits._

        // 检索出top10_users - items
        // 加载Top10用户id （上一任务存储的）
        val top10_user_id = spark
            .table("gz_ds_dws.top10_user")
            .select("user")
            .collect
            .map(row => row.get(0))
        /* 返回数组
        Array(16443, 2251, 3190, 16501, 19334, 15396, 8763, 2713, 11398, 13097)
         */

        // 获取 top10_users-items
        val top10users_items = users_items_df.where(col("user_id").isin(top10_user_id:_*))

        /* 检索出user6708-items */
        // 获取6708用户已购买的商品
        val user6708_items = users_items_df.where($"user_id"===6708)

        /* 从top10_users-items中，剔除掉6708user-items，得到待推荐商品id集 */
        // 前10个用户所购买的商品集
        val top10_sku_ids = top10users_items.select("sku_id").distinct
        // 用户6708所购买的商品集
        val user6708_sku_ids = user6708_items.select("sku_id").distinct

        // 从前10个用户所购买的商品集中剔除用户6708已购买的商品,获得待推荐商品id集
        val candidate_sku_ids = top10_sku_ids.except(user6708_sku_ids)
        candidate_sku_ids.show()


        /* 3. 联合dim_sku_info，得到待推荐商品集 */
        // 从数据仓库加载特征工程预处理后的商品维表 sku_info_cleaned
        val sku_info_cleaned = spark.table("gz_ds_dwd.sku_info_cleaned")

        // 待推荐商品集
        val candidate_sku_info = sku_info_cleaned.join(candidate_sku_ids, "sku_id")

        // 用户6708的商品集
        val user6708_sku_info = sku_info_cleaned.join(user6708_sku_ids, "sku_id")

        /* 4.
        现在任务转换为，计算 candidate_sku_info 与 user6708_sku_info 的余弦相似度累加再求均值，输出相似度前5商品id作为推荐使用。
        思路：
           (1) 找出 candidate_sku_info 中每个sku对应的特征向量（除了sku_id，都是特征）；
           (2) 找出 user6708_sku_info 中每个sku对应的特征向量（除了sku_id，都是特征）；
           (3) 遍历 candidate_sku_info  中每个商品，分别计算其与 user6708_sku_info 中每个商品的余弦相似度，累加这些余弦相似度并求出均值；
           (4) 对相似度均值排名，输出前5个最相似商品作为推荐。
         */
        // (1) 找出 candidate_sku_info 中每个sku对应的特征向量
        import org.apache.spark.ml.feature.VectorAssembler

        // 定义特征属性数组(.tail获取除id外的特征字段)
        val feature_columns = candidate_sku_info.columns.tail

        // 定义一个向量装配器，将各个特征字段装配到特征向量列features
        val assembler = new VectorAssembler().setInputCols(feature_columns).setOutputCol("features")

        // 执行特征向量转换，并仅保留sku_id和features两列即可
        val candidate_sku_with_features = assembler.transform(candidate_sku_info).select("sku_id","features")
        // candidate_sku_with_features.show()
        // candidate_sku_with_features.printSchema()


        // (2) 找出 user6708_sku_info 中每个sku对应的特征向量
        // 重用之前定义的装配器和特征属性列
        val user6708_skus_with_features = assembler.transform(user6708_sku_info).select("sku_id","features")
        // user6708_skus_with_features.show()

        // 提取 user6708用户的商品特征向量
        val user6708_features_array = user6708_skus_with_features
            .select("features")
            .rdd.map{case Row(features:Vector) => features}
            .collect

        // 遍历候选商品，计算其每一个与user6708的每一个商品的余弦相似度，累加这些余弦相似度并求均值
        val result = candidate_sku_with_features.rdd   // 转换为rdd
            .map{case Row(sku_id:Long, features:Vector) => (sku_id, features)}   // 提取商品id列和特征向量列，组成元组元素
            // 计算每个商品的平均余弦相似度
            .map{t =>
                var sum_sim = 0.0
                for(f <- user6708_features_array){
                    sum_sim = sum_sim + cos_sim(f, t._2)     // 累加余弦相似度
                }
                (t._1, sum_sim/user6708_features_array.length)  // 平均余弦相似度
            }
            .sortBy(_._2,false)     // 按平均余弦相似度倒序排列(值越接近1，相似度越高)
            .take(5)

        // result.foreach(println)

        // 输出相似度topN(商品id：1，平均相似度：0.983456)
        var index = 0
        for(t <- result){
            index = index + 1
            println(f"相似度top${index}(商品id：${t._1}，平均相似度：${t._2}%.6f")
        }
        /*
        相似度top1(商品id：1644，平均相似度：0.705021
        相似度top2(商品id：1556，平均相似度：0.703296
        相似度top3(商品id：1671，平均相似度：0.699261
        相似度top4(商品id：4795，平均相似度：0.686571
        相似度top5(商品id：6288，平均相似度：0.686011
         */
    }

    /* 定义求余弦相似度的方法 */
    // 如果是 >= Spark 3.0.0
    def cos_sim(a:Vector,b:Vector):Double = {
        a.dot(b) / (Vectors.norm(a, 2) * Vectors.norm(b, 2))
    }


    // 否则，用笨办法计算余弦相似度
    def calSimilarity(va:Vector,vb:Vector): Double = {
        val a = va.toArray
        val b = vb.toArray

        // 欧几里得公式进行计算
        // 求出分子部分
        val member = a.zip(b).map(num => num._1 * num._2).sum.toDouble

        // 求出分母中第一个变量的值
        val temp1 = math.sqrt(a.map(math.pow(_, 2)).sum)

        // 求出分母中第二个变量的值
        val temp2 = math.sqrt(b.map(math.pow(_, 2)).sum)

        // 求出分母
        val denominator = temp1 * temp2

        // 分子/分母=相似度
        if (denominator == 0) Double.NaN else member / (denominator * 1.0)
        member / denominator
    }
}