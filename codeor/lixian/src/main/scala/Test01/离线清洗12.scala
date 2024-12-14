package Test01

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions.{current_timestamp, date_trunc, lit}

object 离线清洗12 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val sq = SparkSession
                .builder()
                .master("local[*]")
                .appName("spark12")
                .enableHiveSupport()
                .config("hive.exec.scratchdir","hdfs://192.168.45.13:9000/use/hive/temp")
                .config("hive.exec.dynamic.partition.mode","nonstrict")
                .getOrCreate()

        val inft = sq.sql("select * from dwd.dim_customer_inf")
                .drop("etl_date")
                //TODO withColumnRenamed用来重命名列
                .withColumnRenamed("dwd_insert_time", "inf_insert_time")
                .withColumnRenamed("dwd_insert_user", "inf_insert_user")
                .withColumnRenamed("dwd_modify_user", "inf_modify_user")
                .withColumnRenamed("dwd_modify_time", "inf_modify_time")
                .withColumnRenamed("modified_time", "inf_modified_time")

        val addrt = sq.sql("select * from dwd.dim_customer_addr")
                .drop("etl_date")
                .withColumnRenamed("dwd_insert_time", "addr_insert_time")
                .withColumnRenamed("dwd_insert_user", "addr_insert_user")
                .withColumnRenamed("dwd_modify_user", "addr_modify_user")
                .withColumnRenamed("dwd_modify_time", "addr_modify_time")
                .withColumnRenamed("modified_time", "addr_modified_time")

        val levelt = sq.sql("select * from dwd.dim_customer_level_inf")
                .drop("etl_date")
                .withColumnRenamed("dwd_insert_time", "level_insert_time")
                .withColumnRenamed("dwd_insert_user", "level_insert_user")
                .withColumnRenamed("dwd_modify_user", "level_modify_user")
                .withColumnRenamed("dwd_modify_time", "level_modify_time")
                .withColumnRenamed("modified_time", "level_modified_time")

        inft
                .join(addrt,"customer_id")
                .join(levelt,"customer_level")
                .withColumn("dwd_insert_user",lit("user1"))
                .withColumn("dwd_insert_time",date_trunc("second",current_timestamp()))
                .withColumn("dwd_modify_user",lit("user1"))
                .withColumn("dwd_modify_time",date_trunc("second",current_timestamp()))
                .withColumn("etl_date",lit("20241213"))
                .write
                .format("hive")
                .mode(SaveMode.Overwrite)
                .partitionBy("etl_date")
                .saveAsTable("dws.customer_addr_level_aggr")


//        sq.sql(
//            """
//              |select * from
//              |inf as i
//              |join addr as a
//              |on i.customer_id = a.customer_id
//              |""".stripMargin).createTempView("jo")
//
//        sq.sql(
//            """
//              |select * from
//              |jo as j
//              |join level as l
//              |on j.
//              |
//              |""".stripMargin)




    }
}
