package Test04

import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.DataTypes

import java.util.Properties

object 抽取03 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
//        抽取shtd_store库中base_province的增量数据进入Hive的 ods库中表base_province。
//        根据ods.base_province表中id作为增量字段，
//        只将新增的数据抽入，字段名称、类型不变并添加字 段 create_time 取当前时间，
//        同时添加静态分区，分区字段为etl_date，类型为String，
//        且值为当前比赛日的前一天日期（分区字段格式为 yyyyMMdd）。
//        使用hive cli执行show partitions ods.base_province 命令，
//        将结果截图粘贴至客户端桌面【Release\任务B提交结果.docx】中对应的任务序号下；
        System.setProperty("HADOOP_USER_NAME", "root")
        /**
         * spark实例
         */
        val spark = Util.GetSpark
        /**
         * hive库与表
         */
        val hive = "ods.base_province"
        /**
         * mysql库与表
         */
        val mysql = "shtd_store.base_province"
        /**
         * mysql表
         */
        val mysqlTable = "base_province"
        /**
         * mysql数据库
         */
        val mysqlData = "shtd_store"
        /**
         * 增量字段
         */
        val addField = "id"

        Util.MySQlToHive(spark, mysql, hive, addField)
        val hivemaxID = spark.sql(s"select max(id) from ${hive}").first()(0)

        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        spark.read.jdbc(s"jdbc:mysql://192.168.45.13:3306/${mysqlData}?useSSL=false", mysqlTable, conf)
            .where(col("id").cast(DataTypes.LongType) > lit(hivemaxID))
            .withColumn("etl_date", lit("20250305"))
//            .withColumn("create_time", date_format(current_timestamp(), "yyyy-MM-dd HH-mm-ss")) //比赛时根据原始行进行格式化
            .write
            .mode(SaveMode.Append)
            .format("hive")
            .partitionBy("etl_date")
            .saveAsTable(hive)


    }
}

