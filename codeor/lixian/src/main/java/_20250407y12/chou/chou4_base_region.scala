package _20250407y12.chou

import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.DataTypes
import org.apache.spark.sql.{SaveMode, SparkSession}

import java.util.Properties

object chou4_base_region {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession
            .builder()
            .appName("hive")
            .config("hive.exec.dynamic.partition.mode","nonstrict")
            .enableHiveSupport()
            .master("local[*]")
            .getOrCreate()

        //抽取mysql
        val conf = new Properties()
        conf.put("password", "123456")
        conf.put("user", "root")
        val mysql = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "base_region", conf)

        //ods最大
        val odstable = spark.sql("select * from ods.base_region")
        val odsMaxId = odstable.select(max("id")).first()(0)
        val cols = odstable.columns.map(col)

        //mysql增量
        mysql
            .where(col("id") > odsMaxId)
            .withColumn("etl_date", lit("20250406"))
            .withColumn("create_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
            .select(cols:_*)
            .write
            .mode(SaveMode.Append)
            .format("hive")
            .partitionBy("etl_date")
            .saveAsTable("ods.base_region")

    }
}
