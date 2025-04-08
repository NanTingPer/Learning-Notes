package hudi.one

//--conf "spark.sql.extensions=org.apache.spark.sql.hudi.HoodieSparkSessionExtension" \
//--conf "spark.serializer=org.apache.spark.serializer.KryoSerializer" \
object cq6 {
    def main(args: Array[String]): Unit = {
        import org.apache.hudi.DataSourceWriteOptions._
        import org.apache.hudi.QuickstartUtils._
        import org.apache.hudi.config.HoodieWriteConfig._
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.{SaveMode, SparkSession}

        import java.util.Properties
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .master("local[*]")
            .getOrCreate()

        //mysql
        val conf = new Properties();conf.put("user","root");conf.put("password", "123456")
        val mysql = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false",

            "order_detail"

            , conf)

        //ods_ds_hudi
        val user_info = spark
            .read
            .format("hudi")
            .load("hdfs:///user/hive/warehouse/ods_ds_hudi.db/" +

            "order_detail")

            .drop("_hoodie_commit_time")
            .drop("_hoodie_commit_seqno")
            .drop("_hoodie_record_key")
            .drop("_hoodie_partition_path")
            .drop("_hoodie_file_name")

        val cols = user_info.columns.map(col)
        val maxTime = user_info.select(max("create_time")).first()(0)

        //增量
        mysql
            .where(col("create_time") > maxTime)
            .withColumn("etl_date", lit("20250407"))
            .select(cols:_*)
            .write
            .mode(SaveMode.Append)
            .format("hudi")
            .options(getQuickstartWriteConfigs)
            .option(SQL_ENABLE_BULK_INSERT.key(), "true")
            .option(HIVE_STYLE_PARTITIONING.key(), "true")
            .option(RECORDKEY_FIELD.key(), "id")
            .option(PARTITIONPATH_FIELD.key(), "etl_date")
            .option(PRECOMBINE_FIELD.key(), "create_time")
            .option(TBL_NAME.key(),

                "order_detail")
            .save("hdfs:///user/hive/warehouse/ods_ds_hudi.db/" +

                "order_detail"

            )
    }
}
