package _20241220

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions.lit

import java.util
import java.util.Properties

object caiji {
    def main(args: Array[String]): Unit = {
        sparke("order_detail","ods.order_detail")
        sparke("coupon_info","ods.coupon_info")
        sparke("coupon_use","ods.coupon_use")
        sparke("product_browse","ods.product_browse")
        sparke("product_info","ods.product_info")
        sparke("customer_inf","ods.customer_inf")
        sparke("customer_login_log","ods.customer_login_log")
        sparke("order_cart","ods.order_cart")
        sparke("customer_addr","ods.customer_addr")
        sparke("customer_level_inf","ods.customer_level_inf")

    }

    def sparke(mysqltable : String,hivetable : String) = {
        System.setProperty("HADOOP_USER_NAME","root")
        val sp = SparkSession.builder()
                .master("local[*]")
                .appName("ruawf")
                .config("hive.exec.scratchdir","hdfs://192.168.45.20:9000/user/temp/hive")
                .config("hive.exec.dynamic.partition.mode","nonstrict")
                .enableHiveSupport()
                .getOrCreate()

        val myconf = new util.Properties()
        myconf.put("user","root")
        myconf.put("password","123456")
        sp.read.jdbc("jdbc:mysql://192.168.45.20:3306/ds_db01?useSSL=false",s"${mysqltable}", myconf)
                .withColumn("etl_date",lit("20241220"))
                .write
                .mode(SaveMode.Overwrite)
                .format("hive")
                .partitionBy("etl_date")
                .saveAsTable(s"${hivetable}")

        sp.close()


    }


}
