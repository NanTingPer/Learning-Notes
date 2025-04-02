package 样卷2Thre.计算错误03

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions._

import java.util.Properties

object erro03 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession
            .builder()
            .master("local[*]")
            .appName("dawf")
            .enableHiveSupport()
            .getOrCreate()

        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        val order_info = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "order_info", conf).where(year(col("create_time")) === 2020).where(month(col("create_time")) === 4)
        val order_detail = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "order_detail", conf).where(year(col("create_time")) === 2020).where(month(col("create_time")) === 4)
        val base_province = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "base_province", conf)
        val base_region = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "base_region", conf)

        //todo 销量前10
        val xlTop10 = order_detail
            .where(year(col("create_time")) === 2020)
            .groupBy("sku_id")
            .agg(count("*") as "topquantity")
            .withColumn("sequence", row_number().over(Window.orderBy(col("topquantity").desc)))

        val xseTop10 = order_detail
            .where(year(col("create_time")) === 2020)
            .withColumn("je", col("sku_num") * col("order_price"))
            .groupBy("sku_id")
            .agg(sum("je") as "topprice")
            .withColumn("sequence", row_number().over(Window.orderBy(col("topprice").desc)))

        xlTop10.join(xseTop10, "sequence").orderBy("sequence").show
    }
}

//1、
//      请根据dwd层表计算出2020年4月每个省份的平均订单金额和所有省份平均订单金额相比较结果（“高/低/相同”）,
//      存入MySQL数据库shtd_result的provinceavgcmp表（表结构如下）中，
//      然后在Linux的MySQL命令行中根据省份表主键、该省平均订单金额均为降序排序，
//      查询出前5条，
//      将SQL语句复制粘贴至客户端桌面【Release\任务B提交结果.docx】中对应的任务序号下，
//      将执行结果截图粘贴至客户端桌面【Release\任务B提交结果.docx】中对应的任务序号下;
