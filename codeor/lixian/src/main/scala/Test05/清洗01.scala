package Test05

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.functions.coalesce
import org.apache.spark.sql.expressions._

object 清洗01 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val  spark = SparkSession.builder()
            .enableHiveSupport().appName("hive")
            .config("hive.exec.dynamic.partition.mode","nonstrict")
            .master("local[*]")
            .getOrCreate()

        val odstable = "ods.user_info"
        val dwdtable = "dwd.dim_user_info"

        var odsdata = spark.sql(s"select *, coalesce(operate_time, create_time) as temp from ${odstable} where etl_date='20250305'")
            .drop("operate_time")
            .withColumnRenamed("temp", "operate_time")
        val maxtime = spark.sql(s"select max(etl_date) from ${dwdtable}")
        val dwddata = spark.sql(s"select * from ${dwdtable} where etl_date='${maxtime}'")
        val allcol = dwddata.columns.map(col)

        val win1 = Window.partitionBy("id").orderBy("operate_time")
        odsdata = odsdata
                    .withColumn("dim_insert_user", lit("user1"))
                    .withColumn("dim_insert_time", date_format(current_timestamp(),"yyyy-MM-dd HH:mm:ss"))
                    .withColumn("dim_modify_user", lit("user1"))
                    .withColumn("dim_modify_time", date_format(current_timestamp(),"yyyy-MM-dd HH:mm:ss"))
                    .select(allcol:_*)

        odsdata.union(dwddata)
            .withColumn("tempcol", row_number().over(win1))
            .withColumn("dim_insert_time", min(col("dim_insert_time")).over(win1))
            .withColumn("dim_modify_time", max(col("dim_modify_time")).over(win1))
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
