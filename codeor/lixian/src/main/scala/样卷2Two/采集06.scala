package 样卷2Two

object 采集06 {
    def main(args: Array[String]): Unit = {
        import org.apache.hudi.DataSourceWriteOptions._
        import org.apache.hudi.QuickstartUtils._
        import org.apache.hudi.config.HoodieWriteConfig.TBL_NAME
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.{SaveMode, SparkSession}

        import java.util.Properties
        val spark = SparkSession
            .builder()
            .master("local[*]")
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .config("spark.sql.extension", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
            .enableHiveSupport()
            .getOrCreate()
        val conf = new Properties()
        conf.put("user", "root")
        conf.put("password","123456")
        val mysqldata = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "order_detail", conf)
        val hudidata = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/ods_ds_hudi.db/order_detail")

        val maxtime = hudidata.select(max(col("create_time"))).first()(0)
        val updata = mysqldata
            .where(col("create_time") > maxtime)
            .withColumn("etl_date", lit("20250325"))
            .write
            .format("hudi")
            .options(getQuickstartWriteConfigs)
            .option(RECORDKEY_FIELD.key,"id")
            .option(PRECOMBINE_FIELD.key,"create_time")
            .option(PARTITIONPATH_FIELD.key,"etl_date")
            .option(HIVE_STYLE_PARTITIONING.key,"true")
            .option(SQL_ENABLE_BULK_INSERT.key,"true")
            .option(TBL_NAME.key, "order_detail")
            .mode(SaveMode.Append)
            .save("hdfs:///user/hive/warehouse/ods_ds_hudi.db/order_detail")

    }
}
