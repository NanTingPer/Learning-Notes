package _20241220

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions.{col, current_timestamp, date_trunc, lit}

object qinxi11 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val sp = SparkSession.builder()
                .master("local[*]")
                .appName("ruawf")
                .config("hive.exec.scratchdir","hdfs://192.168.45.20:9000/user/temp/hive")
                .config("hive.exec.dynamic.partition.mode","nonstrict")
                .enableHiveSupport()
                .getOrCreate()

        val inf = sp.sql("select * from dwd.dim_customer_inf")
                .withColumnRenamed("dwd_insert_user", "inf_insert_user")
                .withColumnRenamed("dwd_insert_time", "inf_insert_time")
                .withColumnRenamed("dwd_modify_user", "inf_modify_user")
                .withColumnRenamed("dwd_modify_time", "inf_modify_time")
                .withColumnRenamed("etl_date","e")
                .withColumnRenamed("modified_time","etime")

        val addr = sp.sql("select * from dwd.dim_customer_addr")
                .withColumnRenamed("dwd_insert_user", "addr_insert_user")
                .withColumnRenamed("dwd_insert_time", "addr_insert_time")
                .withColumnRenamed("dwd_modify_user", "addr_modify_user")
                .withColumnRenamed("dwd_modify_time", "addr_modify_time")
                .withColumnRenamed("etl_date","e1")
                .withColumnRenamed("modified_time","e1time")

        val level = sp.sql("select * from dwd.dim_customer_level_inf")
                .withColumnRenamed("dwd_insert_user", "level_insert_user")
                .withColumnRenamed("dwd_insert_time", "level_insert_time")
                .withColumnRenamed("dwd_modify_user", "level_modify_user")
                .withColumnRenamed("dwd_modify_time", "level_modify_time")
                .withColumnRenamed("etl_date","e2")
                .withColumnRenamed("modified_time","e2time")

        inf.join(addr,"customer_id").join(level,"customer_level")
                .withColumn("etl_date",lit("20241220"))
                .withColumn("dws_insert_user",lit("user1"))
                .withColumn("dws_insert_time",date_trunc("second",current_timestamp()))
                .withColumn("dws_modify_user",lit("user1"))
                .withColumn("dws_modify_time",date_trunc("second",current_timestamp()))
                .write
                .mode(SaveMode.Overwrite)
                .format("hive")
                .partitionBy("etl_date")
                .saveAsTable("dws.customer_addr_level_aggr")


    }
}
