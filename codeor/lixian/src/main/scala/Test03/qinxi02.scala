package Test03

import org.apache.spark.sql.{SaveMode, SparkSession, functions}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

import java.time.LocalTime

object qinxi02 {
    def main(args: Array[String]): Unit = {
        System  .setProperty("HADOOP_USER_NAME","root")

        val sp = SparkSession.builder()
                .enableHiveSupport()
                .config("hive.exec.scratchdir","hdfs://192.168.45.13:9000/user/hive/temp")
                .config("hive.exec.dynamic.partition.mode","nonstrict")
                .master("local[*]")
                .appName("dwaf")
                .getOrCreate()

        val newtime = sp.sql("select max(etl_date) from ods.coupon_inf").first()(0)
        val newtable = sp.sql(s"select * from ods.coupon_inf where etl_date = '${newtime}'")

        val oldtime = sp.sql("select max(etl_date) from dwd.dim_coupon_info").first()(0)
        val oldtable = sp.sql(s"select * from dwd.dim_coupon_info where etl_date = ${oldtime}")
        val cols = oldtable.columns.map(col(_))

        val newnewtable = newtable
                .withColumn("dwd_insert_user", lit("user1"))
                .withColumn("dwd_insert_time", date_trunc("second", current_timestamp()))
                .withColumn("dwd_modify_user", lit("user1"))
                .withColumn("dwd_modify_time", date_trunc("second", current_timestamp()))
                .select(cols: _*)

        val untable = oldtable.union(newnewtable)

        val win1 = Window.partitionBy("coupon_id").orderBy(col("modified_time").desc)
        val win2 = Window.partitionBy("coupon_id")

        newnewtable
                .withColumn("c",row_number().over(win1))
                .withColumn("dwd_insert_time",min("dwd_insert_time").over(win2))
                .withColumn("dwd_modify_time",max("dwd_modify_time").over(win2))
                .createTempView("temp")
        sp.sql("select * from temp where c = 1").drop(col("c"))
                .write
                .mode(SaveMode.Overwrite)
                .format("hive")
                .insertInto("dwd.dim_coupon_info")

    }
}
