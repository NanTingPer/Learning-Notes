package Test07

import org.apache.spark.sql.functions._
import org.apache.spark.sql.{SaveMode, SparkSession}

object P02 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession.builder()
            .appName("hive")
            .enableHiveSupport()
            .config("hive.exec.dynamic.partition.mode","nonstrict")
            .master("local[*]")
            .getOrCreate()

        val ods = "ods." + "sku_info"
        val dwd = "dwd." + "dim_sku_info"

        spark.sql(s"select * from ${ods}")
            .withColumn("dwd_insert_user", lit("user1"))
            .withColumn("dwd_insert_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss"))
            .withColumn("dwd_modify_user", lit("user1"))
            .withColumn("dwd_modify_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss"))
            .write
            .mode(SaveMode.Overwrite)
            .format("hive")
            .partitionBy("etl_date")
            .saveAsTable(dwd)
    }

}
