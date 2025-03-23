package Test09

import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.functions._

object QX04 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = Utile.GetSpark
        val odstable = "ods.order_detail"
        val dwdtable = "dwd.fact_order_detail"
        val ods = spark.table(odstable).where(col("etl_date") === "20250321")

        val lengthr = udf((s : String) => s.length <= 8)

        ods
            .withColumn("dwd_insert_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss"))
            .withColumn("dwd_insert_user",lit("user1"))
            .withColumn("dwd_modify_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss"))
            .withColumn("dwd_modify_user",lit("user1"))
            .withColumn("etl_date", substring(col("create_time"),1,8))
            .write
            .mode(SaveMode.Overwrite)
            .format("hive")
            .partitionBy("etl_date")
            .saveAsTable(dwdtable)
    }
}
