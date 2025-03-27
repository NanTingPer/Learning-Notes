package 样卷2Two

object 采集03 {
    def main(args: Array[String]): Unit = {
        import org.apache.hudi.DataSourceWriteOptions._
        import org.apache.hudi.QuickstartUtils._
        import org.apache.hudi.config.HoodieWriteConfig.TBL_NAME
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.{SaveMode, SparkSession}

        import java.util.Properties
        val spark = SparkSession
            .builder()
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
            .enableHiveSupport()
            .getOrCreate()
        val conf = new Properties()
        conf.put("user", "root")
        conf.put("password","123456")
        val mysqldata = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "base_province", conf)
        val hudidata = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/ods_ds_hudi.db/base_province")

        val maxid = hudidata.select(max(col("id"))).first()(0)
        mysqldata
            .where(col("id") > maxid)
            .withColumn("etl_date", lit("20250325"))
            .withColumn("create_time", date_format(current_timestamp(), "yyyy-MM-dd HH-mm-ss"))
            .write
            .format("hudi")
            .options(getQuickstartWriteConfigs)
            .option(RECORDKEY_FIELD.key,"id")
            .option(PRECOMBINE_FIELD.key,"create_time")
            .option(PARTITIONPATH_FIELD.key,"etl_date")
            .option(HIVE_STYLE_PARTITIONING.key,"true")
            .option(SQL_ENABLE_BULK_INSERT.key,"true")
            .option(TBL_NAME.key, "base_province")
            .mode(SaveMode.Append)
            .save("hdfs:///user/hive/warehouse/ods_ds_hudi.db/base_province")
    }
}
