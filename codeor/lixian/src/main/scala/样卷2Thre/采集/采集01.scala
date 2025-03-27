package 样卷2Thre.采集

object 采集01 {
    def main(args: Array[String]): Unit = {
        import org.apache.hudi.DataSourceWriteOptions._
        import org.apache.hudi.QuickstartUtils._
        import org.apache.hudi.config.HoodieWriteConfig.TBL_NAME
        import org.apache.spark.sql.{SaveMode, SparkSession}
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.types.DataTypes

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
        val sqldata = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "user_info", conf)
        val hudidata = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/ods_ds_hudi.db/user_info")
            .drop("_hoodie_commit_time","_hoodie_commit_seqno","_hoodie_record_key","_hoodie_partition_path","_hoodie_file_name")
        val cols = hudidata.columns.map(col)
        val maxtime = hudidata.select(max(greatest(col("operate_time"), col("create_time")))).first()(0)

        sqldata.where(greatest(col("operate_time"), col("create_time")) > maxtime)
            .withColumn("operate_time", coalesce(col("operate_time"),col("create_time")))
            .withColumn("etl_date", lit("20250326"))
            .withColumn("operate_time", col("operate_time").cast(DataTypes.TimestampType))
            .select(cols:_*)
            .write
            .mode(SaveMode.Append)
            .format("hudi")
            .options(getQuickstartWriteConfigs)
            .option(RECORDKEY_FIELD.key,"id")
            .option(PARTITIONPATH_FIELD.key,"etl_date")
            .option(PRECOMBINE_FIELD.key,"operate_time")
            .option(SQL_ENABLE_BULK_INSERT.key,"true")
            .option(HIVE_STYLE_PARTITIONING.key,"true")
            .option(TBL_NAME.key,"user_info")
            .save("hdfs:///user/hive/warehouse/ods_ds_hudi.db/user_info")
    }
}
