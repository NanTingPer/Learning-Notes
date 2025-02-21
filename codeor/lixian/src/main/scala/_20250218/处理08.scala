package _20250218

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions.{current_timestamp, date_format, lit}

object 处理08 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME", "root")
        val spark = SparkSession
            .builder()
            .appName("dawf")
            .master("local[*]")
            .enableHiveSupport()
            .getOrCreate()

        val maxtimeCol = spark.sql("select max(etl_date) from ods.order_cart").first()(0)
        spark
            .sql(s"select * from ods.order_cart where etl_date = '${maxtimeCol}'")
            .withColumn("dwd_insert_user",lit("user1"))
            .withColumn("dwd_insert_time",date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss"))
            .withColumn("dwd_modify_user",lit("user1"))
            .withColumn("dwd_modify_time",date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss"))
            .write
            .mode(SaveMode.Overwrite)
            .format("hive")
            .partitionBy("etl_date")
            .saveAsTable("dwd.fact_order_cart")
    }

}
