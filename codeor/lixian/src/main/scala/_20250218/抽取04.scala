package _20250218

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions.lit

import java.util.Properties

object 抽取04 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val c = new Properties()
        c.put("user","root")
        c.put("password","123456")
        SparkSession.builder().enableHiveSupport().master("local[*]")
                .appName("dawf")
                .config("hive.exec.dynamic.partition.mode","nonstrict")
                .getOrCreate().read
                .jdbc("jdbc:mysql://192.168.45.13:3306/ds_db01?useSSL=false","coupon_use",c)
                .withColumn("etl_date",lit("20250218"))
                .write.format("hive")
                .partitionBy("etl_date")
                .mode(SaveMode.Overwrite)
                .saveAsTable("ods.coupon_use")
    }

}
