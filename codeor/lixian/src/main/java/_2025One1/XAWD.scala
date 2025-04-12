package _2025One1

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.sql.SparkSession

object XAWD {
    def main(args: Array[String]): Unit = {
        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .appName("hudi")
            .getOrCreate()

        val zx = spark.table("ods.zxc")
        val vectorsRDD = zx.drop("user_id")
            .rdd
            .map(row => Vectors.dense(row.toSeq.map(_.asInstanceOf[Double]).toArray))
            .cache()
        val rowMatrix = new RowMatrix(vectorsRDD)
        val SVD = rowMatrix.computeSVD(5, computeU = true) //5是奇异值信息

        val qyz = SVD.s //奇异值
        val u = SVD.U //降维后的数据
    }
}
