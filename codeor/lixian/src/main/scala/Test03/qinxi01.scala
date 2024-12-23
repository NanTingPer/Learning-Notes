package Test03

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.{col, current_timestamp, date_trunc, lit, max, min, row_number}

import java.time.LocalTime

object qinxi01 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")

        val sp = SparkSession.builder()
                .enableHiveSupport()
                .appName("dawf")
                .config("hive.exec.scratchdir","hdfs://192.168.45.13:9000/user/hive/temp")
                .config("hive.exec.dynamic.partition.mode","nonstrict")

                .master("local[*]")
                .getOrCreate()

        val oldtime = sp.sql("select max(etl_date) from dwd.dim_customer_inf")
        val olddata = sp.sql(s"select * from dwd.dim_customer_inf where etl_date = '${oldtime}'")
        val allcol = olddata.columns.map(col(_))

        val newtime = sp.sql("select max(etl_date) from ods.customer_inf")
        val newdata = sp.sql(s"select * from ods.customer_inf where etl_date = '${newtime}'")

        val win1 = Window.partitionBy("customer_id").orderBy(col("modified_time").desc)
        val win2 = Window.partitionBy("customer_id")

        val newnewtable = newdata
                .withColumn("dwd_insert_user", lit("user1"))
                .withColumn("dwd_insert_time", date_trunc("second", current_timestamp()))
                .withColumn("dwd_modify_user", lit("user1"))
                .withColumn("dwd_modify_time", date_trunc("second", current_timestamp()))
                .select(allcol: _*)

        olddata.union(newnewtable)
                .withColumn("count",row_number().over(win1))
                //不变
                .withColumn("dwd_insert_time",min("dwd_insert_time").over(win2))
                .withColumn("dwd_modify_time",max("dwd_modify_time").over(win2))
//                .withColumn("etl_date",lit(newtime.toString()))
                .createTempView("fitable")

        sp.sql("select * from fitable where count = 1").drop(col("count"))
                .write
                .format("hive")
                .mode(SaveMode.Overwrite)
//                .partitionBy("etl_date")
                .insertInto("dwd.dim_customer_inf")



    }
}
