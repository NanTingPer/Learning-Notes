package _20241220

import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{SaveMode, SparkSession}

object qinxi2 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val sp = SparkSession.builder()
                .master("local[*]")
                .appName("ruawf")
                .config("hive.exec.scratchdir","hdfs://192.168.45.20:9000/user/temp/hive")
                .config("hive.exec.dynamic.partition.mode","nonstrict")
                .enableHiveSupport()
                .getOrCreate()

        val mtime = sp.sql("select max(etl_date) from ods.coupon_info").first()(0)
        val newdate = sp.sql(s"select * from ods.coupon_info where etl_date = '${mtime}'")

        val otime = sp.sql("select max(etl_date) from dwd.dim_coupon_info").first()(0)
        val olddate = sp.sql(s"select * from dwd.dim_coupon_info where etl_date = '${otime}'")
        val allcol = olddate.columns.map(col(_))

        val nutable = newdate
                .withColumn("dwd_insert_user", lit("user1"))
                .withColumn("dwd_insert_time", date_trunc("second", current_timestamp()))
                .withColumn("dwd_modify_user", lit("user1"))
                .withColumn("dwd_modify_time", date_trunc("second", current_timestamp()))
                .select(allcol: _*)
                .union(olddate)

        val win1 = Window.partitionBy("coupon_id").orderBy("modified_time")
        val win2 = Window.partitionBy("coupon_id")

        nutable
                .withColumn("c",row_number().over(win1))
                .withColumn("dwd_insert_time",min("dwd_insert_time").over(win2))
                .withColumn("dwd_modify_time",max("dwd_modify_time").over(win2))
                .createTempView("table1")

        sp.sql("select * from table1 where c = 1")
                .drop(col("c"))
                .withColumn("etl_date",lit(newdate.toString()))
                .write
                .mode(SaveMode.Overwrite)
                .format("hive")
//                .partitionBy("etl_date")
                .insertInto("dwd.dim_coupon_info")
    }

}
