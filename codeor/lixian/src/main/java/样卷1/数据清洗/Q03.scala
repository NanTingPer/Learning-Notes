package 样卷1.数据清洗

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions._
import org.apache.spark.sql.types.DataTypes

object Q03 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME", "root")

        val spark = SparkSession
            .builder()
            .appName("hive")
            .master("local[*]")
            .enableHiveSupport()
            .getOrCreate()

        //todo ods最新数据
        val `ods.base_province` = spark.sql("select * from ods.base_province") //spark.table("ods.base_province")
        val `ods.base_province_newPartition` = `ods.base_province`.select(max("etl_date")).first()(0)
        val OdsNewData = `ods.base_province`.where(col("etl_date") === `ods.base_province_newPartition`)

        val `dwd.dim_province` = spark.table("dwd.dim_province")
        val dwdNewPartition = `dwd.dim_province`.select(max("etl_date")).first()(0)
        val DwdNewData = `dwd.dim_province`.where(col("etl_date") === dwdNewPartition)
        val cols = DwdNewData.columns.map(col)
        DwdNewData.limit(1).show

        val odsNewData = OdsNewData
            .withColumn("dwd_modify_user", lit("user1"))
            .withColumn("dwd_modify_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
            .withColumn("dwd_insert_user", lit("user1"))
            .withColumn("dwd_insert_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
            .select(cols: _*)
        odsNewData.limit(1).show

        val unionTable = odsNewData.union(DwdNewData)
        val win1 = Window.partitionBy("id").orderBy(col("create_time").desc)
        val win2 = Window.partitionBy("id")

        unionTable
            .withColumn("temp",row_number().over(win1))
            .withColumn("dwd_modify_time", max(col("dwd_modify_time")).over(win2))
            .withColumn("dwd_insert_time", min(col("dwd_insert_time")).over(win2))
//            .where(col("temp") === 1)
            .filter(f => f.getAs("temp") == 1)
            .show
//            .write
//            .mode(SaveMode.Append)
//            .format("hive")
//            .saveAsTable("dwd.dim_province")

    }
}