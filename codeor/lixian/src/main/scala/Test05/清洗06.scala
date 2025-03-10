package Test05

import org.apache.spark.sql.functions._
import org.apache.spark.sql.{SaveMode, SparkSession}

object 清洗06 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val  spark = SparkSession.builder()
            .enableHiveSupport().appName("hive")
            .config("hive.exec.dynamic.partition.mode","nonstrict")
            .master("local[*]")
            .getOrCreate()

        val odstable = "ods.order_detail"
        val dwdtable = "dwd.fact_order_detail"

        val odsdata =    spark.sql(s"select *,coalesce(operate_time, create_time) as tem from ${odstable} where etl_date = '20250305'")
            .drop("operate_time")
            .withColumnRenamed("tem", "operate_time")
            .withColumn("etl_date", coalesce(date_format(col("create_time"), "yyyyMMdd"), lit("20250305")))
            .withColumn("dim_insert_user", lit("user1"))
            .withColumn("dim_insert_time", date_format(current_timestamp(),"yyyy-MM-dd HH:mm:ss"))
            .withColumn("dim_modify_user", lit("user1"))
            .withColumn("dim_modify_time", date_format(current_timestamp(),"yyyy-MM-dd HH:mm:ss"))
            .write
            .mode(SaveMode.Overwrite)
            .partitionBy("etl_date")
            .format("hive")
            .saveAsTable(dwdtable)


        //导入数据
//        spark.sql(s"select * from ${odstable} limit 10")
//            .withColumn("dim_insert_user", lit("user1"))
//            .withColumn("dim_insert_time", date_format(current_timestamp(),"yyyy-MM-dd HH:mm:ss"))
//            .withColumn("dim_modify_user", lit("user1"))
//            .withColumn("dim_modify_time", date_format(current_timestamp(),"yyyy-MM-dd HH:mm:ss"))
//            .withColumn("etl_date", lit("20250303"))
//            .write
//            .mode(SaveMode.Append)
//            .partitionBy("etl_date")
//            .format("hive")
//            .saveAsTable(dwdtable)
    }
}
