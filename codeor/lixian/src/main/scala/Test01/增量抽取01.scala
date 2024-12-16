package Test01

import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.{SaveMode, SparkSession}

import java.sql.Timestamp
import java.util.Properties

//TODO 抽取ds_db01 库中order_master 的增量数据
//TODO 进入Hive 的ods 库中表order_master。
//TODO 根据ods.order_master 表中modified_time 作为增量字段，
//TODO 只将新增的数据抽入，字段名称、类型不变，
//TODO 同时添加静态分区，分区字段 为etl_date，
//TODO 类型为String，且值为当前比赛日的前一天日期（分区字段格式为yyyyMMdd）。
//TODO 使用hive cli 执行show partitions ods.order_master命令，
//TODO 将执行结果截图粘贴至客户端桌面【Release\模块B 提交结果.docx】中对应的任务序号下；


//TODO 需要注意的是 Spark的Hive配置在resources的hive-site.xml
//TODO 所以代码中基本不需要配置
object 增量抽取01 {
    def main(args: Array[String]): Unit = {
        //TODO 根据ods.order_master 表中modified_time 作为增量字段
        //TODO 获取hive存量最大值
        //TODO first() 获取第一行
        //TODO getAs[T] 将数据转换为T类型 (0) 是获取第一列的值

        //TODO 抽取ds_db01 库中order_master 的增量数据
        //TODO 进入Hive 的ods 库中表order_master。

        //TODO 没表
        SaveTable("ds_db01","order_master","ods","order_master","modified_time","etl_data")

        //TODO 任务4
//        SaveTable(spark,"ds_db01","coupon_use","ods","coupon_use","")
//
        SaveTable("ds_db01","order_detail","ods","order_detail","modified_time","etl_data")

        SaveTable("ds_db01","coupon_info","ods","coupon_info","modified_time","etl_data")

        SaveTable("ds_db01","product_browse","ods","product_browse","modified_time","etl_data")

        SaveTable("ds_db01","product_info","ods","product_info","modified_time","etl_data")

        SaveTable("ds_db01","customer_inf","ods","customer_inf","modified_time","etl_data")

        SaveTable("ds_db01","customer_login_log","ods","customer_login_log","login_time","etl_data")

        SaveTable("ds_db01","order_cart","ods","order_cart","modified_time","etl_data")

        SaveTable("ds_db01","customer_addr","ods","customer_addr","modified_time","etl_data")

        SaveTable("ds_db01","customer_level_inf","ods","customer_level_inf","modified_time","etl_data")


    }

    def SaveTable(MySQL库名:String, MySQL表名:String, 目标库 : String, 目标表 : String, 增量字段 : String , 分区名称 : String):Unit ={
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession.builder()
                .master("local[*]")
                .appName("666")
                .config("hive.exec.scratchdir","hdfs://192.168.45.13:9000/user/hive/temp")
                .config("hive.exec.dynamic.partition.mode","nonstrict")
                .enableHiveSupport()
                .getOrCreate()


        var MaxTime = spark.sql(s"select max(${增量字段}) from ${目标库}.${目标表} ").first().getAs[Timestamp](0)
        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        val frame = spark.read.jdbc(s"jdbc:mysql://192.168.45.13:3306/${MySQL库名}?useSSL=false", MySQL表名, conf)
        frame.createOrReplaceTempView("mysql")

        spark.sql(s"select * from mysql where ${增量字段} > '${MaxTime}'")
                .withColumn(分区名称,lit("20241208"))
                .write
                .format("hive")
                .mode(SaveMode.Overwrite)
                .partitionBy(分区名称)
                .saveAsTable(目标库 + "." + 目标表)
        println("\t\t\t\t\t\t\t\t完成")
        println("-----------------------------------------------------------------------------------------")
        println("-----------------------------------------------------------------------------------------")
        println("-----------------------------------------------------------------------------------------")
        println("-----------------------------------------------------------------------------------------")
        spark.close();
    }


}
