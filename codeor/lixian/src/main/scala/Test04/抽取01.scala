package Test04

import org.apache.spark.sql.functions._

object 抽取01 {
//    抽取shtd_store库中user_info的增量数据进入Hive的ods库 中表user_info。
//    根据ods.user_info表中operate_time或create_time作为增量字段
//    ·(即MySQL中每条数据取这两个时间中较 大的那个时间作为增量字段去和ods里的这两个字段中较大的时间进行比较)，
//    只将新增的数据抽入，字段名称、类型不变，
//    同时添加静态分区，分区字段为etl_date，
//    类型为 String，
//    且值为当前比赛日的前一天日期（分区字段格式为 yyyyMMdd）。
//    使用hive cli执行show partitions ods.user_info命令，
//    将结果截图粘贴至客户端桌面【Release\任务B提交结果.docx】中对应的任务序号下；
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val Mdatabases = "shtd_store.user_info"
        val Hdatabases = "ods.user_info"
        val spark = Util.GetSpark
        Util.MySQlToHive(spark, Mdatabases, Hdatabases, "operate_time")
        val maxtime = spark.sql("select greatest(max(operate_time), max(create_time)) from ods.user_info").first()(0)


        val mySQLData = Util.GetMySQLData(spark, "shtd_store", "user_info")
        println("Hive最大时间: " + maxtime)       //Hive最大时间: 2020-04-26 18:57:55.0
        val sqlmaxtime = mySQLData.select(greatest(max("operate_time"), max("create_time"))).first()(0)
        println("myql最大时间: " + sqlmaxtime)  //mySql最大时间: 2020-04-26 23:54:52.0
        mySQLData.where(greatest(col("operate_time"),col("create_time")) > maxtime).show
    }
}
