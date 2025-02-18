package _20250218

import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.{SaveMode, SparkSession}

import java.util.Properties

object 抽取11 {
    /**
     * Copy
     * @param args
     */
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val c = new Properties()
        c.put("user","root")
        c.put("password","123456")

        SparkSession
                .builder()
                .master("local[*]")
                .appName("dawf")
//                .config("spark.sql.partitionOverwriteMode", "dynamic")
                .config("hive.exec.dynamic.partition.mode","nonstrict")
                .config("spark.sql.partitionOverwriteMode", "dynamic")
                .enableHiveSupport()
                .getOrCreate()
                .read
                .jdbc("jdbc:mysql://192.168.45.13/ds_db01?useSSL=false","customer_level_inf",c)
                .withColumn("etl_date",lit("20250218"))
                .write
                .mode(SaveMode.Overwrite)
                .partitionBy("etl_date")
                .format("hive")
                .saveAsTable("ods.customer_level_inf")
    }

}
