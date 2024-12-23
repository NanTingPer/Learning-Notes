package _20241220

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.{col, count, lit, row_number, sum}
import org.apache.spark.sql.types.DataTypes

import java.util.Properties

object jisuan02 {
    def main(args: Array[String]): Unit = {
            System.setProperty("HADOOP_USER_NAME", "root")
            val sp = SparkSession.builder()
                    .master("local[*]")
                    .appName("ruawf")
                    .config("hive.exec.scratchdir", "hdfs://192.168.45.20:9000/user/temp/hive")
                    .config("hive.exec.dynamic.partition.mode", "nonstrict")
                    .enableHiveSupport()
                    .getOrCreate()


            val master = sp.sql("select * from dwd.fact_order_master where order_status = '已付款'")
            val detail = sp.sql("select * from dwd.fact_order_detail")

            val masterytk = sp.sql("select * from dwd.fact_order_master where order_status = '已退款'")

            val 有用的表 = master.except(masterytk)

            val end = 有用的表.join(detail, "order_sn")


            end.withColumn("sales_amount", lit(col("product_cnt") * col("product_price"))
                    .cast(DataTypes.createDecimalType(8, 2)))
                    .createTempView("jeb")

            val win1= Window.partitionBy("product_id").orderBy("sales_amount")
            val win2= Window.partitionBy("product_id")
            val win3= Window.orderBy("sales_amount")

            val 表 = sp.sql(
                """
                  |select product_id,sales_amount,product_cnt
                  |from jeb
                  |""".stripMargin)

            表
                    .withColumn("product_totalcnt",sum(col("product_cnt")).over(win2))
                    .withColumn("paim",row_number().over(win1))
                    .createTempView("rrrr")

                    sp.sql("select * from rrrr where paim = 1")
                    .withColumn("sales_rank",row_number().over(win3))
                            .createTempView("wwwww")

        val config = new Properties()
        config.put("user","default")
        config.put("password","123456")


        sp.sql(
            """
              |select product_id,sales_amount,product_totalcnt,sales_rank
              |from wwwww
              |""".stripMargin)
                .write.mode(SaveMode.Append).jdbc("jdbc:clickhouse://192.168.45.20:8123/ds_result","sales_amount_rank",config)





    }

}
