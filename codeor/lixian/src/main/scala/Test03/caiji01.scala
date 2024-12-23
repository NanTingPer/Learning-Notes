package Test03

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions.lit

import java.util.Properties

object caiji01 {
    def main(args: Array[String]): Unit = {
        run("modified_time","ods.order_detail","order_detail","etl_date")
        run("modified_time","ods.coupon_info","coupon_info","etl_date")
        run("modified_time","ods.product_browse","product_browse","etl_date")
        run("modified_time","ods.product_info","product_info","etl_date")
        run("modified_time","ods.customer_inf","customer_inf","etl_date")
        run("modified_time","ods.customer_login_log","customer_login_log","etl_date")
        run("modified_time","ods.order_cart","order_cart","etl_date")
        run("modified_time","ods.customer_addr","customer_addr","etl_date")
        run("modified_time","ods.customer_level_inf","customer_level_inf","etl_date")

    }
    def run (upData : String,fromtable : String,mysqltable : String,byFild : String)={
        System.setProperty("HADOOP_USER_NAME","root")

        val spark = SparkSession.builder()
                .master("local[*]")
                .appName("dawd")
                .config("hive.exec.scratchdir","hdfs://192.168.45.13:9000/user/hive/temp")
                .config("hive.exec.dynamic.partition.mode","nonstrict")
                .enableHiveSupport()
                .getOrCreate()

//        val maxtime = spark.sql(s"select max(${upData}) from ${fromtable}").first()(0)

        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")

        spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/ds_db01?useSSL=false",s"${mysqltable}",conf)
                .createTempView("mysql")

        spark.sql(s"select * from mysql")// where ${upData} > '${maxtime}'
                .withColumn(s"${byFild}",lit("20241219"))
                .write
                .format("hive")
                .partitionBy(s"${byFild}")
                .mode(SaveMode.Overwrite)
                .saveAsTable(s"${fromtable}")

        spark.close()


    }

}
