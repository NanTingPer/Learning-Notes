package Test04

import org.apache.spark.sql.SaveMode

import java.util.Properties
import org.apache.spark.sql.functions._

object 抽取04 {
//    抽取shtd_store库中base_region的增量数据进入Hive的ods 库中表base_region。
//    根据ods.base_region表中id作为增量字段，
//    只将新增的数据抽入，字段名称、类型不变并添加字段crea te_time取当前时间，
//    同时添加静态分区，分区字段为etl_date，类型为String，
//    且值为当前比赛日的前一天日期（分区字段格式为yyyyMMdd）。
//    使用hive cli执行show partitions ods.base_region命令，
//    将结果截图粘贴至客户端桌面【Release\任务B提交结果.docx】中对应的任务序号下；
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = Util.GetSpark
        Util.MySQlToHive(spark, "shtd_store.base_region", "ods.base_region", "id")
        val hivemaxtime = spark.sql("select max(id) from ods.base_region").first()(0)
        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")

        spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "base_region", conf)
//            .withColumn("create_time", date_format(current_timestamp(), "yyyy-MM-dd HH-mm-ss"))
            .withColumn("etl_date", lit("20250304"))
            .write
            .mode(SaveMode.Append)
            .format("hive")
            .partitionBy("etl_date")
            .saveAsTable("ods.base_region")
    }
}
