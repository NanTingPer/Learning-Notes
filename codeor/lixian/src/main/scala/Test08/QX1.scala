package Test08

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions._

import java.util.Properties

object QX1 {
    def getSpark : SparkSession = {
        val spark = SparkSession.builder()
            .master("local[*]")
            .appName("d")
            .config("hive.exec.dynamic.partition.mode","nonstrict")
            .enableHiveSupport()
            .getOrCreate()
        spark
    }

    def getsqlConf() : Properties = {
        val conf = new Properties()
        conf.put("user", "root")
        conf.put("password", "123456")
        conf
    }

    def dataToHive(spark : SparkSession, odsHive : String, dwdHive : String) ={
        spark.table(odsHive).where(col("etl_date") === "20250316")
            .withColumn("dwd_insert_user", lit("user1"))
            .withColumn("dwd_insert_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss"))
            .withColumn("dwd_modify_user", lit("user1"))
            .withColumn("dwd_modify_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss"))
            .withColumn("etl_date", lit("20250315"))
            .limit(10)
            .write
            .mode(SaveMode.Overwrite)
            .format("hive")
            .partitionBy("etl_date")
            .saveAsTable(dwdHive)

    }

    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME", "root")
        val odstable = "ods.user_info"
        val dwdtable = "dwd.dim_user_info"
        val spark = getSpark

        dataToHive(spark, odstable, dwdtable)


        val odsdata = spark.table(odstable).where(col("etl_date") === "20250316")

        odsdata.show
        var dwddata = spark.table(dwdtable)
        dwddata = dwddata.where(col("etl_date") === dwddata.select(max("etl_date")).first()(0))
        val cols = dwddata.columns.map(col)

        val win1 = Window.partitionBy("id").orderBy(col("operate_time").desc)
        val win2 = Window.partitionBy("id")

        val newodsdata = odsdata
            .withColumn("temp_operate_time", coalesce(col("operate_time"), col("create_time")))
            .drop("operate_time")
            .withColumnRenamed("temp_operate_time", "operate_time")
            .withColumn("dwd_insert_user", lit("user1"))
            .withColumn("dwd_insert_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss"))
            .withColumn("dwd_modify_user", lit("user1"))
            .withColumn("dwd_modify_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss"))
            .select(cols:_*)

        val untable = newodsdata.union(dwddata)

        untable
            .withColumn("temp", row_number().over(win1))
            .withColumn("dwd_insert_time", min("dwd_insert_time").over(win2))
            .withColumn("dwd_modify_time", max("dwd_modify_time").over(win2))
            .where(col("temp") === 1)
            .withColumn("dwd_insert_user", lit("user1"))
            .withColumn("dwd_insert_time", coalesce(col("dwd_insert_time"), date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss")))
            .withColumn("dwd_modify_user", lit("user1"))
            .withColumn("dwd_modify_time", coalesce(col("dwd_modify_time"), date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss")))
            .drop("temp")
            .withColumn("etl_date",lit("20250316"))
            .write
            .mode(SaveMode.Append)
            .format("hive")
            .partitionBy("etl_date")
            .saveAsTable(dwdtable)
    }

}
