package _20250224

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.DataTypes
object LXCL01 {
    def main(args: Array[String]): Unit = {
        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .appName("LXCL01")
            .master("local[*]")
            .getOrCreate()

        val maxtime = spark.sql("select max(etl_date) from ods.customer_inf").first()(0)
        //也叫新数据
        val odsData = spark.sql(s"select * from ods.customer_inf where etl_date = '${maxtime}'")

        val maxtime2 = spark.sql("select max(etl_date) from dwd.dim_customer_inf").first()(0)
        val dwdDate = spark.sql(s"select * from dwd.dim_customer where etl_date ='${maxtime2}'")
        //用于对齐
        val cols = dwdDate.columns.map(col)

        val win1 = Window.partitionBy("customer_id").orderBy(col("modified_time").desc)
        val win2 = Window.partitionBy("customer_id")

        odsData
            .withColumn("dwd_insert_user",lit("user1"))
            .withColumn("dwd_insert_time",lit("20250224"))
            .withColumn("dwd_modify_user",lit("user1"))
            .withColumn("dwd_modify_time",lit("20250224"))
            .select(cols:_*)
            .union(dwdDate)
            .withColumn("temp",row_number().over(win1))
            .withColumn("dwd_insert_time",min(col("dwd_insert_time")).over(win2))
            .withColumn("dwd_modify_time",min(col("dwd_modify_time")).over(win2))
            .where(col("temp") === 1)
            .drop(col("temp"))
            .drop(col("etl_date"))
            .withColumn("etl_date",lit(maxtime).cast(DataTypes.StringType))
            .write
            .mode(SaveMode.Overwrite)
            .format("hive")
            .partitionBy("etl_date")
    }
}
