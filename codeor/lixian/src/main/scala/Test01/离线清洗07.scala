package Test01

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions.{current_timestamp, date_trunc, lit}

import java.time.LocalTime

object 离线清洗07 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val sq = SparkSession.builder()
                .master("local[*]")
                .appName("spark07")
                .config("hive.exec.scratchdir","hdfs://192.168.45.13:9000/use/hive/temp")
                .config("hive.exec.dynamic.partition.mode","nonstrict")
                .enableHiveSupport()

                .getOrCreate()

        val mt = sq.sql("select max(etl_date) from ods.customer_login_log").first()(0)
        sq.sql(s"select * from ods.customer_login_log where etl_date = '${mt}'")
                .withColumn("dwd_insert_user",lit("user1"))
                .withColumn("dwd_insert_time",date_trunc("second",current_timestamp()))
                .withColumn("dwd_modify_user",lit("user1"))
                .withColumn("dwd_modify_time",date_trunc("second",current_timestamp()))
                .write
                .mode(SaveMode.Overwrite)
                .format("hive")
                .partitionBy("etl_date")
                .saveAsTable("dwd.log_customer_login")
    }
}
