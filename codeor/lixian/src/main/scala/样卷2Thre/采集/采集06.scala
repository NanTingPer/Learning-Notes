package 样卷2Thre.采集

object 采集06 {
    def main(args: Array[String]): Unit = {
        import org.apache.hudi.DataSourceWriteOptions
        import org.apache.hudi.QuickstartUtils._
        import org.apache.hudi.config.HoodieWriteConfig.TBL_NAME
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.types.DataTypes
        import org.apache.spark.sql.{SaveMode, SparkSession}

        import java.util.Properties

        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
            .master("yarn")
            .appName("awfawf")
            .getOrCreate()

        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        val sqldata = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "order_detail", conf)
        val hudidata = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/ods_ds_hudi.db/order_detail")
            .drop("_hoodie_commit_time","_hoodie_commit_seqno","_hoodie_record_key","_hoodie_partition_path","_hoodie_file_name")

        val cols = hudidata.columns.map(col)
        val maxid = hudidata.select(max(col("create_time"))).first()(0)
        val update = sqldata.where(col("create_time") > maxid)
            .withColumn("etl_date", lit("20250326"))
            .withColumn("create_time", col("create_time").cast(DataTypes.TimestampType))
            .select(cols:_*)
        update.show
        update
            .write
            .format("hudi")
            .options(getQuickstartWriteConfigs)
            .option(DataSourceWriteOptions.RECORDKEY_FIELD.key(),"id")
            .option(DataSourceWriteOptions.PRECOMBINE_FIELD.key(),"create_time")
            .option(DataSourceWriteOptions.PARTITIONPATH_FIELD.key(),"etl_date")
            .option(DataSourceWriteOptions.HIVE_STYLE_PARTITIONING.key(),"true")
            .option(DataSourceWriteOptions.SQL_ENABLE_BULK_INSERT.key(),"true")
            .mode(SaveMode.Append)
            .option(TBL_NAME.key(),"order_detail")
            .save("hdfs:///user/hive/warehouse/ods_ds_hudi.db/order_detail")
    }
}
