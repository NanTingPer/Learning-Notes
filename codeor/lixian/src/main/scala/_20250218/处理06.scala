package _20250218

import org.apache.spark.sql.functions.{current_timestamp, date_format, lit}
import org.apache.spark.sql.{Column, SaveMode, SparkSession}

object 处理06 {

    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession
            .builder()
            .appName("dawf")
            .master("local[*]")
            .enableHiveSupport()
            .getOrCreate()

        val maxtime = spark.sql("select max(etl_date) from ods.coupon_use").first()(0)
        spark
            .sql(s"select * from ods.coupon_use where etl_date='${maxtime}'")
            .withColumn("dwd_insert_user",lit("user1"))
            .withColumn("dwd_insert_time",date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss"))
            .withColumn("dwd_modify_user",lit("user1"))
            .withColumn("dwd_modify_time",date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss"))
            .write
            .mode(SaveMode.Append)
            .format("hive")
            .partitionBy("etc_date")
            .saveAsTable("dwd.fact_coupon_use")
    }
}
