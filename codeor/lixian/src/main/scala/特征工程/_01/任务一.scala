package 特征工程._01

import org.apache.spark.sql.SparkSession

object 任务一 {
    //剔除订单主表与订单详情表中用户id与商品id不存在现有的维表中的记录，
    //同时建议多利用缓存并充分考虑并行度来优化代码，达到更快的计算效果。
    def main(args: Array[String]): Unit = {
        val spark = SparkSession.builder()
    }
}
