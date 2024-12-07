import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.{SaveMode, SparkSession}

import java.util.Properties

object customer_inf增量数据 {
  def main(args: Array[String]): Unit = {
    System.setProperty("HADOOP_USER_NAME","root")
    /*
    抽取ds_db01 库中customer_inf 的增量数据进入Hive 的ods 库中表customer_inf，
    根据ods.customer_inf 表中modified_time 作为增量字段，只将新增的数据抽入，
    字段名称、类型不变，同时添加静态分区，分区字段为etl_date，类型为String，
    且值为当前比赛日的前一天日期（分区字段格式为yyyyMMdd）。使用hive cli 执行show partitions ods.customer_inf命令，
    将执行结果截图粘贴至客户端桌面【Release\模块B 提交结果.docx】中对应的任务序号下；
     */
    var spark = SparkSession.builder().master("local[*]").appName("customer").enableHiveSupport().getOrCreate();
    var SQLConfig = new Properties();
    SQLConfig.put("user","root")
    SQLConfig.put("password","123456")
    //获取SQL的数据表 并创建视图方便使用
    val SQLTable = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/ds_db01?useSSL=false", "customer_inf", SQLConfig);
    SQLTable.createOrReplaceTempView("SQLTable");
    //获取最大的modified字段
    val MaxTime = spark.sql("select max(modified_time) from ods.customer_inf").rdd.first().get(0)

    val 增量数据 = spark.sql(s"select * from SQLTable where modified_time > '${MaxTime}'")
      .withColumn("etl_date",lit("20241029"))
      .write
      .mode(SaveMode.Overwrite)
      .partitionBy("etl_date")
      .saveAsTable("ods.customer_inf");

    spark.table("ods.customer_inf").show;
  }
}
