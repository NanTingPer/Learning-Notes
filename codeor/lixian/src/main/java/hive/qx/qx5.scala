package hive.qx

object qx5 {
    def main(args: Array[String]): Unit = {

        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.types.DataTypes
        import org.apache.spark.sql.{SaveMode, SparkSession}
        val spark = SparkSession
            .builder()
            .config("hive.exec.dynamic.partition.mode","nonstrict")
            .master("local[*]")
            .appName("hive")
            .enableHiveSupport()
            .getOrCreate()

        //ods
        spark.table("ods.order_info").where(col("etl_date") === "20250407")
            .withColumn("operate_time", coalesce(col("operate_time"), col("create_time")))
            .withColumn("dwd_insert_user", lit("user1"))
            .withColumn("dwd_insert_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
            .withColumn("dwd_modify_user", lit("user1"))
            .withColumn("dwd_modify_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
            .withColumn("etl_date", date_format(col("create_time"), "yyyyMMdd"))
            .write
            .mode(SaveMode.Append)
            .format("hive")
            .partitionBy("etl_date")
            .saveAsTable("dwd.fact_order_info")

    }
}
