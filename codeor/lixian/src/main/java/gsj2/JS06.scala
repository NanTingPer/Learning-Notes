package gsj2

import org.apache.spark.sql.{Dataset, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions._

import java.util.Properties

object JS06 {
    def main(args: Array[String]): Unit = {
        val spark = SparkSession
            .builder()
            .master("local[*]")
            .appName("hive")
            .enableHiveSupport()
            .getOrCreate()

        val conf = new Properties()
        conf.put("user", "root")
        conf.put("password", "123456")
        val o1 = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "order_info", conf).where(col("id") <= 20000).orderBy("id").select("id", "final_total_amount")

//        o1.orderBy("id").show

        val win1 = Window.orderBy("id")
        val winTable = o1
            .withColumn("row_num", row_number().over(win1))
            .withColumn("sum_total_amount", sum("final_total_amount").over(win1))
            .withColumn("count_order", count("*").over(win1))

        //计算连续序列
        val JoinTable = winTable
            //todo 更名好join
            .as("o1")
            .withColumnRenamed("row_num", "o1_row_num")
            .withColumnRenamed("sum_total_amount", "o1_sum_total_amount")
            .withColumnRenamed("count_order", "o1_count_order")
            .withColumnRenamed("id", "o1_id")

            //todo 自join 取出连续订单序列
            //  - 一表 - 自己的id >= 200
            //  - 一表id - 自己的id = 一表排名 - 自己的排名
            .join(winTable as "o2", col("o2.id") >= col("o1_id") + 200 and col("o1_id") - col("o2.id") === col("o1_row_num") - col("o2.row_num"))
            .orderBy("o1_id")

        JoinTable
            //todo 计算序列平均订单金额
            .withColumn("avg_amount", abs(col("o1_sum_total_amount") - col("sum_total_amount")) / abs(col("o1_id") - col("id")))

            //todo 因为是连续序列，直接减就是序列长度
            .withColumn("matchnum", abs(col("o1_id") - col("id")))
            .select("o1_id", "id", "avg_amount", "matchnum")

            //todo 金额数相同的连续订单，保留订单数最少的
            .withColumn("avg_order_drop_num", rank().over(Window.partitionBy("avg_amount").orderBy("matchnum")))
            .where(col("avg_order_drop_num") === 1)

            //todo 按照平均金额排序，如果相同，按照订单表主键降序排序
            .withColumn("seq_index", row_number().over(Window.orderBy(col("avg_amount").desc, col("o1_id").desc)))

            //todo 插入字段
            .withColumn("id_range", concat(col("o1_id"), lit("_"), col("id")))
            .select("seq_index", "avg_amount", "id_range", "matchnum")
            .withColumnRenamed("avg_amount", "avg_order_price")
            .orderBy("seq_index")
            .where(col("seq_index") === 1 or col("seq_index") === 10 or col("seq_index") === 100 or col("seq_index") === 1000 or col("seq_index") === 100000)
            .show

        //TODO
        // 若有多个平均金额数相同的连续订单序列，
        // 要求输出连续订单数最少的订单序列，
        // 若多个平均金额数相同的连续订单序列中的订单个数同样相同，
        // 则根据订单表主键集合进行字典降序排序

    }
}
