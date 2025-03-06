package Test05

import org.apache.spark.sql.expressions._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{SaveMode, SparkSession}

object 清洗03 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val  spark = SparkSession.builder()
            .enableHiveSupport().appName("hive")
            .config("hive.exec.dynamic.partition.mode","nonstrict")
            .master("local[*]")
            .getOrCreate()

        val odstable = "ods.base_province"
        val dwdtable = "dwd.base_province"

        var odsdata = spark.sql(s"select * from ${odstable} where etl_date='20250305'")
        val maxtime = spark.sql(s"select max(etl_date) from ${dwdtable}")
        val dwddata = spark.sql(s"select * from ${dwdtable} where etl_date='${maxtime}'")
        val allcol = dwddata.columns.map(col)

        val win1 = Window.partitionBy("id").orderBy("create_time")
        odsdata = odsdata
                    .withColumn("dwd_insert_user", lit("user1"))
                    .withColumn("dwd_insert_time", date_format(current_timestamp(),"yyyy-MM-dd HH:mm:ss"))
                    .withColumn("dwd_modify_user", lit("user1"))
                    .withColumn("dwd_modify_time", date_format(current_timestamp(),"yyyy-MM-dd HH:mm:ss"))
                    .select(allcol:_*)

        odsdata.union(dwddata)
            .withColumn("tempcol", row_number().over(win1))
            .withColumn("dwd_insert_time", min(col("dwd_insert_time")).over(win1))
            .withColumn("dwd_modify_time", max(col("dwd_modify_time")).over(win1))
            .withColumn("dwd_insert_time", coalesce(col("dwd_insert_time"), date_format(current_timestamp(),"yyyy-MM-dd HH:mm:ss")))
            .withColumn("dwd_modify_time", coalesce(col("dwd_modify_time"), date_format(current_timestamp(),"yyyy-MM-dd HH:mm:ss")))
            .withColumn("dwd_insert_user", lit("user1"))
            .withColumn("dwd_modify_user", lit("user1"))
            .where(col("tempcol") === 1)
            .drop("tempcol")
            .withColumn("etl_date", lit("20250305"))
            .write
            .mode(SaveMode.Append)
            .partitionBy("etl_date")
            .format("hive")
            .saveAsTable(dwdtable)

        //导入数据
//        spark.sql(s"select * from ${odstable} limit 10")
//            .withColumn("dwd_insert_user", lit("user1"))
//            .withColumn("dwd_insert_time", date_format(current_timestamp(),"yyyy-MM-dd HH:mm:ss"))
//            .withColumn("dwd_modify_user", lit("user1"))
//            .withColumn("dwd_modify_time", date_format(current_timestamp(),"yyyy-MM-dd HH:mm:ss"))
//            .withColumn("etl_date", lit("20250303"))
//            .write
//            .mode(SaveMode.Append)
//            .partitionBy("etl_date")
//            .format("hive")
//            .saveAsTable(dwdtable)
    }
}
