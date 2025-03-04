package Test04

import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.functions._

import java.util.Properties

object 抽取06 {
    def main(args: Array[String]): Unit = {
//        抽取shtd_store库中order_detail的增量数据进入Hive的 ods库中表order_detail，
//        根据ods.order_detail表中create_time作为增量字段，
//        只将新增的数据抽入，字段名称、类型不变，同时添加静态分区，
//        分区字段为etl_date，类型为String，
//        且值为当前比赛日的前一天日期（分区字段格式为yyyyMMdd）。
//        使用hive cli执行 show partitions ods.order_detail命令，将
//        结果截图粘贴至客户端桌面【Release\任务B提交结果.docx】中对应的任务序号下
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = Util.GetSpark
        Util.MySQlToHive(spark, "shtd_store.order_detail", "ods.order_detail", "create_time")
        val hivemaxtime = spark.sql("select create_time from ods.order_detail").first()(0)
        val conf = new Properties()
        conf.put("user","root")
            conf.put("password","123456")
        spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "order_detail",conf)
            .withColumn("etl_date", lit("20250304"))
            .write
            .mode(SaveMode.Append)
            .format("hive")
            .partitionBy("etl_date")
            .saveAsTable("ods.order_detail")


    }
}
