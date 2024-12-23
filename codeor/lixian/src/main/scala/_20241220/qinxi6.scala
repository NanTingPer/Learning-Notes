package _20241220

import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{SaveMode, SparkSession}

object qinxi6 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val sp = SparkSession.builder()
                .master("local[*]")
                .appName("ruawf")
                .config("hive.exec.scratchdir","hdfs://192.168.45.20:9000/user/temp/hive")
                .config("hive.exec.dynamic.partition.mode","nonstrict")
                .enableHiveSupport()
                .getOrCreate()

        val mtime = sp.sql("select max(etl_date) from ods.customer_addr").first()(0)
        val newdate = sp.sql(s"select * from ods.customer_addr where etl_date = '${mtime}'")

        val nutable = newdate
                .withColumn("dwd_insert_user", lit("user1"))
                .withColumn("dwd_insert_time", date_trunc("second", current_timestamp()))
                .withColumn("dwd_modify_user", lit("user1"))
                .withColumn("dwd_modify_time", date_trunc("second", current_timestamp()))
                .withColumn("etl_date",lit(mtime.toString()))
                .write
                .mode(SaveMode.Overwrite)
                .format("hive")
                .partitionBy("etl_date")
                .saveAsTable("dwd.dim_customer_addr")
    }

}
