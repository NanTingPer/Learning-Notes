package _2025One1

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.sql.SparkSession

object XAWD2 {
    def main(args: Array[String]): Unit = {
        import org.apache.spark.sql.SparkSession
        import org.apache.spark.ml.feature.VectorAssembler
        import org.apache.spark.mllib.linalg.distributed.RowMatrix
        import org.apache.spark.mllib.linalg.{Vector => OldVector, Vectors => OldVectors, Matrix}
        import org.apache.spark.broadcast.Broadcast
        import org.apache.spark.rdd.RDD

        val spark = SparkSession.builder()
            .appName("Recommendation")
            .enableHiveSupport()
            .getOrCreate()

        // 读取one-hot矩阵
        val df = spark.sql("SELECT * FROM ods.zxc")
        val featureCols = df.columns.filter(_.startsWith("sku_id")).sorted

        // 转换为特征向量
        val assembler = new VectorAssembler().setInputCols(featureCols).setOutputCol("features")
        val features = assembler.transform(df).select("features")

        // 创建RowMatrix并计算SVD
        val rows = features.rdd.map(r => OldVectors.fromML(r.getAs[org.apache.spark.ml.linalg.Vector]("features")))
        val matrix = new RowMatrix(rows)
        val svd = matrix.computeSVD(5, computeU = true)

        // 获取商品特征矩阵并广播
        val skuVectors = (0 until svd.V.numRows).map(i =>
            OldVectors.dense((0 until svd.V.numCols).map(j => svd.V(i, j)).toArray)
        ).toArray
        val bcVectors = spark.sparkContext.broadcast(skuVectors)

        // 生成用户购买记录
        val userPurchases = df.rdd.map { row =>
            val userId = row.getAs[Double]("user_id_mapping").toInt
            val purchased = featureCols.zipWithIndex.collect {
                case (col, idx) if row.getAs[Double](col) == 1.0 => idx
            }
            (userId, purchased)
        }

        // 计算余弦相似度
        def cosineSim(v1: OldVector, v2: OldVector): Double = {
            val dot = v1.dot(v2)
            val norm = OldVectors.norm(v1, 2) * OldVectors.norm(v2, 2)
            if (norm == 0) 0.0 else dot / norm
        }

        // 为每个用户生成推荐
        val recommendations = userPurchases.flatMap { case (userId, purchased) =>
            val purchasedSet = purchased.toSet
            val allSkus = bcVectors.value.indices
            val candidates = allSkus.filterNot(purchasedSet.contains)

            candidates.map { skuId =>
                val similarities = purchased.map(p => cosineSim(bcVectors.value(skuId), bcVectors.value(p)))
                val avgSim = similarities.sum / similarities.size
                (userId, skuId, avgSim)
            }.sortBy(-_._3).take(5)
        }

        // 打印结果
        recommendations.sortBy(f => f._1).collect().take(5).foreach { recs =>
            println("------------------------推荐Top5结果如下------------------------")
            println("相似度top: " + recs._1 + "  商品id: " + recs._2  + "  平均度: " + recs._3)
        }

        spark.stop()

    }
}
