package Test04
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.functions._
object 抽取02 {
//    抽取shtd_store库中sku_info的增量数据进入Hive的ods库中 表 sku_info。
//    根据od s.sku_info表中create_time作为增量字段，
//    只将新增的数据抽入，字段名称、类型不变，同时添加静态分区，分区字段为etl_date，类型为String，
//          且值为当前比赛日的前一天日期（分区字段格式为yyyyMMdd）。
//    使用hive cli执行show partitions ods.sku_info命令，将结果截图粘贴至客户端桌面【Release\任务 B 提交结果.docx】中对应的任务序号下；
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = Util.GetSpark
        val hive = "ods.sku_info"
        Util.MySQlToHive(spark, "shtd_store.sku_info", "ods.sku_info", "create_time")
        val sqlqdata = Util.GetMySQLData(spark, "shtd_store", "sku_info").createTempView("mysqld")

        val hivemaxtime = spark.sql("select  max(create_time) as maxtime from ods.sku_info")/*.select(date_sub(col("maxtime"), days =  1))*/.first()(0)
        println("hive最大时间: " + hivemaxtime) //hive最大时间: 2021-01-01 12:21:13.0
        val sqlmaxtime = spark.sql("select max(create_time) from mysqld").first()(0)
        println("mys最大时间: " + sqlmaxtime)   //hive最大时间: 2021-01-01 12:21:13.0


        spark.sql(s"select * from mysqld where create_time > '${hivemaxtime}'")
            .withColumn("etl_date", lit("20250304"))
            .write
            .partitionBy("etl_date")
            .mode(SaveMode.Append)
            .format("hive")
            .saveAsTable(hive);
    }
}
