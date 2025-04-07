package 样卷1.数据清洗

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions._

object Q02 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession
            .builder()
            .appName("hive")
            .master("local[*]")
            .enableHiveSupport()
            .getOrCreate()

        //todo 抽取ods库数据 并插入字段
        val odsTable = spark
            .sql("select * from ods.sku_info")
            .withColumn("dwd_insert_user", lit("user1"))
            .withColumn("dwd_insert_time", date_trunc("yyyy-MM-dd HH:mm:ss", current_timestamp()))
            .withColumn("dwd_modify_user", lit("user1"))
            .withColumn("dwd_modify_time", date_trunc("yyyy-MM-dd HH:mm:ss", current_timestamp()))
        val odsMaxPartitionValue = odsTable.select(max("etl_date")).first()(0)
        val odsNewData = odsTable.where(col("etl_date") === odsMaxPartitionValue)

        //todo 抽取dwd库数据
        val dwdTable = spark.sql("select * from dwd.dim_sku_info")
        val dwdMaxPartitionValue = dwdTable.select(max("etl_date")).first()(0)
        val dwdNewData = dwdTable.where(col("etl_date") === dwdMaxPartitionValue)
        val cols = dwdNewData.columns.map(col)

        //todo 堆ods数据进行列对其
        val newOdsTable = odsTable.select(cols: _*)
        val untable = newOdsTable.union(dwdNewData)

        val win1 = Window.partitionBy("id").orderBy(col("create_time").desc)
        val win2 = Window.partitionBy("id")

        untable
            .withColumn("temp", row_number().over(win1))
            .withColumn("dwd_insert_time", min(col("dwd_insert_time")).over(win2))
            .withColumn("dwd_modify_time", max(col("dwd_modify_time")).over(win2))
            .where(col("temp") === 1)
            .drop("temp")
            .write
            .format("hive")
            .mode(SaveMode.Append)
            .saveAsTable("dwd.dim_sku_info")

    }
}
