package hudi.js

object j2j2 {
    def main(args: Array[String]): Unit = {
        import org.apache.hudi.DataSourceWriteOptions._
        import org.apache.hudi.QuickstartUtils._
        import org.apache.hudi.config.HoodieWriteConfig._
        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.types.DataTypes
        import org.apache.spark.sql.{SaveMode, SparkSession}
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .master("local[*]")
            .getOrCreate()

        val region = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_region")
        val province = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_province")
        val user_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_user_info")
        val order_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_info")

        val fin = order_info
            .withColumn("year", year(col("create_time")))
            .withColumn("month", month(col("create_time")))
            .withColumn("day", date_format(col("create_time"), "dd"))
            .groupBy("user_id", "year", "month", "day")
            .agg(count("*") as "total_count", sum("final_total_amount") as "total_amount")
            .join(user_info, col("id") === col("user_id"))
            .withColumn("uuid", expr("uuid()"))
            .select("user_id","uuid", "name", "total_amount", "total_count", "year", "month", "day")
            .withColumnRenamed("name", "user_name")
            .withColumn("total_count", col("total_count").cast(DataTypes.IntegerType))
            .withColumn("user_id", col("user_id").cast(DataTypes.IntegerType))
            .withColumn("uuid", col("uuid").cast(DataTypes.StringType))
            .withColumn("total_amount", col("total_amount").cast(DataTypes.DoubleType))

        fin
            .write
            .mode(SaveMode.Append)
            .format("hudi")
            .options(getQuickstartWriteConfigs)
            .option(SQL_ENABLE_BULK_INSERT.key(), "true")
            .option(HIVE_STYLE_PARTITIONING.key(), "true")
            .option(RECORDKEY_FIELD.key(), "uuid")
            .option(PARTITIONPATH_FIELD.key(), "year, month, day")
            .option(PRECOMBINE_FIELD.key(), "total_count")
            .option(TBL_NAME.key(), "user_consumption_day_aggr")
            .save("hdfs:///user/hive/warehouse/dws_ds_hudi.db/user_consumption_day_aggr")
    }
}
