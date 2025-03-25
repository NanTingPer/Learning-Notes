package 特征工程._01

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql._
import org.apache.spark.sql.types._

object 任务一 {

//    根据Hive的dwd库中相关表或MySQL中shtd_store中相关表（order_detail、sku_info），
//    计算出与用户id为6708的用户所购买相同商品种类最多的前10位用户
//    （只考虑他俩购买过多少种相同的商品，不考虑相同的商品买了多少次），
//    将10位用户id进行输出，若与多个用户购买的商品种类相同，
//    则输出结果按照用户id升序排序，输出格式如下，
//    将结果截图粘贴至客户端桌面【Release\任务C提交结果.docx】中对应的任务序号下；
//    结果格式如下：
//    -------------------相同种类前 10 的 id 结果展示为----------
//    ----------
//    1,2,901,4,5,21,32,91,14,52

    /**
     *  SPU 商品名称 128G的手机和256G的手机共用一个SPU
     *  SKU 一类商品
     *  SN   精确到实物
     * @param args
     */
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = Test04.Util.GetSpark
        //    计算出与用户id为6708的用户所购买相同商品种类最多的前10位用户
        //    （只考虑他俩购买过多少种相同的商品，不考虑相同的商品买了多少次）

        //SPUID  应该就是商品ID 在 sku_info
        Test04.Util.GetMySQLData(spark, "shtd_store", "order_detail").createTempView("order_detail")
        Test04.Util.GetMySQLData(spark, "shtd_store", "sku_info").createTempView("sku_info")
        Test04.Util.GetMySQLData(spark, "shtd_store", "order_info").createTempView("order_info")

        spark.sql("select * from dwd.dim_sku_info where etl_date = '20250304'").createTempView("dim_sku_info")
        val order_detail = spark.sql("select * from order_detail")
        //order_detail.id == order_info.id
        //sku_info.id == order_detail.sku_id
        spark.sql(
            """
              | select ori.user_id, oi.order_id, oi.sku_id, si.sku_name , si.category3_id  from order_detail as oi
              | join
              |     sku_info as si
              | on
              |     oi.sku_id = si.id
              | join
              |     order_info as ori
              | on
              |     ori.id = oi.order_id
              |""".stripMargin).createTempView("join")

        // user_id  category3  1605catgory3
        val _1605 = spark.sql("select user_id,category3_id from join where user_id = 1605").groupBy("user_id").agg(collect_set("category3_id").as("1605Item")).first()(1)
        val rdd = spark.sql("select user_id,category3_id from join where user_id <> 1605").groupBy("user_id").agg(collect_set("category3_id").as("userItem"))
            .withColumn("1605Item", lit(_1605)).rdd
        import spark.implicits._
        val top10Users = rdd
            .map(t => (t(0).toString,t.getAs[Seq[String]](1),t.getAs[Seq[String]](2)))
            .map(t => (t._1, t._2, t._3, t._2.intersect(t._3)))  // 转换为(user,其他用户items, 1605用户items, 共同items)
            .map(t => (t._1, 1-t._4.length.toFloat/(t._2.length+t._3.length-t._4.length)))   // 计算jaccard距离
            .toDF("user","jaccard-distance")    // 转换为DataFrame
            .orderBy($"jaccard-distance")
            .take(10)
    println("--------------------------------------------------")
    println("--------------------------------------------------")
    println("--------------------------------------------------")
    println("--------------------------------------------------")
    println("-------------------相同种类前 10 的 id 结果展示为--------------------")
        for(row <- top10Users){
            print(row.get(0) + ",")
        }
        println()


    }
}
