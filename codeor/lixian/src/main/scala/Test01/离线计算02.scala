package Test01

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

import java.util.Properties

// 编写Scala 工程代码，根据dwd 的表，
// 计算所有订单中各商品所有订单总销售金额排名，
// 并将计算结果写入clickhouse 的ds_result 库的表。
// 然后在Linux 的clickhouse 命令行中根据sales_rank 升序查询前5 行，
// 将SQL 语句与执行结果截图粘贴至客户端桌面【Release\模块D 提交结果.docx】中对应的任务序号下；
object 离线计算02 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val sp = SparkSession.builder()
                .master("local[*]")
                .appName("spakrlx02")
                .enableHiveSupport()
                .getOrCreate()

        sp.sql("select * from dwd.fact_order_detail").createTempView("allProduct")

        sp.sql("select product_id,product_name,product_cnt,product_price from allProduct").createTempView("filtration")

        //数量乘以金额
        sp.sql("select * from filtration")
                .withColumn("product_money",lit(col("product_cnt") * col("product_price")))
                .groupBy("product_id")
                .agg(sum("product_money") as "producttotal")
                .createTempView("aggTable")

        val win = Window.orderBy(col("producttotal").desc)

        val conf = new Properties()
        conf.put("password","123456")
        conf.put("user","default")


        sp.sql("select * from aggTable")
                .withColumn("productrank",row_number().over(win))
                .write
                .jdbc("jdbc:clickhouse://192.168.45.13:9001/ds_result","productsalesrank",conf)

    }
}
