package Test01

import org.apache.spark.sql.functions._
import org.apache.spark.sql.{SaveMode, SparkSession}

import java.util.Properties

object 离线计算03 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val sp = SparkSession.builder()
                .master("local[*]")
                .appName("spark03")
                .config("hive.exec.scratchdir","hdfs://192.168.45.13:9000/user/hive/temp")
                .config("hive.exec.dynamic.partition.mode","nonstrict")
                .enableHiveSupport()
                .getOrCreate()

//        val conf = new Properties()
//        conf.put("user","root")
//        conf.put("password","123456")
//
//        sp.read.jdbc("jdbc:mysql://192.168.45.13:3306/ds_db01?useSSL=false","customer_login_log",conf)
//                .write
//                .mode(SaveMode.Overwrite)
//                .format("hive")
//                .saveAsTable("dwd.customer_login_log")

        sp.sql("select * from dwd.customer_login_log").createTempView("hive")
        sp.sql("select max(login_time) as t from hive").createTempView("maxtime")

        val mintime = sp.sql(s"select * from maxtime")
                .withColumn("time", date_sub(col("t"), 21))
                .first()(1)

        sp.sql(s"select * from hive where login_time >= '${mintime}'")
                .withColumn("login_time",col("login_time").substr(1,10))
                .groupBy("customer_id")
                //countDistinct 计算不同值的数量
                .agg(countDistinct(col("login_time")).as("login_count"))
                .where(col("login_count") >= 3)
                .show()

    }
}
