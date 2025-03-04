package Test04
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.functions._

import java.util.Properties
object 抽取05 {
    def main(args: Array[String]): Unit = {
//抽取shtd_store库中order_info的增量数据进入Hive的ods 库中表order_info，
        // 根据ods.order_info表中operate_time或
        //create_time作为增量字段
// (即MySQL中每条数据取这两个时间中较大的那个时间作为增量字段去和ods里的这两个字段中较大的时间进行比较)，
        // 只将新增的数据抽入，字段名称、类型不变，同时添加静态分区，分区字段为etl_date，类型为String，
// 且值为当前比赛日的前一天日期（分区字段格式为 yyyyMMdd）。使用hive cli执行show partitions ods.order_info命令，
// 将结果截图粘贴至客户端桌面【Release\任务B提交结果.docx】中对应的任务序号下；

        System.setProperty("HADOOP_USER_NAME","root")
        val spark = Util.GetSpark
        Util.MySQlToHive(spark, "shtd_store.order_info", "ods.order_info", "create_time")
        val hivemaxtime = spark.sql("select greatest(max(operate_time), max(create_time)) from ods.order_info").first()(0)
        val conf = new Properties()
        conf.put("user","root")
            conf.put("password","123456")
        spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "order_info",conf)
            .where(greatest("operate_time","create_time") > hivemaxtime)
            .withColumn("etl_date", lit("20250304"))
            .write
            .mode(SaveMode.Append)
            .format("hive")
            .partitionBy("etl_date")
            .saveAsTable("ods.order_info")


    }
}
