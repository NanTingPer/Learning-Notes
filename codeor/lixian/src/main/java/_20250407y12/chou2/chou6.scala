package _20250407y12.chou2

object chou6 {
    //spark-sql \
    //--conf "spark.sql.extensions=org.apache.spark.sql.hudi.HoodieSparkSessionExtension" \
    //--conf "spark.serializer=org.apache.spark.serializer.KryoSerializer" \
    //--jars /hudi-spark3.1-bundle_2.12-0.12.0.jar
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        import org.apache.hudi.DataSourceWriteOptions._
        import org.apache.hudi.QuickstartUtils._
        import org.apache.hudi.config.HoodieWriteConfig._
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.types.DataTypes
        import org.apache.spark.sql.{SaveMode, SparkSession}

        import java.util.Properties

        val spark = SparkSession
            .builder()
            .appName("hive")
            .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .enableHiveSupport()
            .master("local[*]")
            .getOrCreate()

        //抽取mysql
        val conf = new Properties()
        conf.put("password", "123456")
        conf.put("user", "root")
        val mysql = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "order_detail", conf)

        //ods最大
        val odstable = spark.read.load("hdfs:///user/hive/warehouse/ods_ds_hudi.db/order_detail")
          .drop("_hoodie_commit_time", "_hoodie_commit_seqno", "_hoodie_record_key", "_hoodie_partition_path", "_hoodie_file_name")
        val odsmaxTime = odstable.select(max("create_time")).first()(0)
        val cols = odstable.columns.map(col)
        odstable.limit(1).show()
        //mysql增量
        mysql
          .where(col("create_time") > odsmaxTime)
            .withColumn("etl_date", lit("20250406"))
          .withColumn("create_time", col("create_time").cast(DataTypes.TimestampType))
          .select(cols:_*)
            .write
            .mode(SaveMode.Append)
            .format("hudi")
            .options(getQuickstartWriteConfigs)
            .option(SQL_ENABLE_BULK_INSERT.key(), "true")
            .option(HIVE_STYLE_PARTITIONING.key(), "true")
            .option(PARTITIONPATH_FIELD.key(), "etl_date")
            .option(RECORDKEY_FIELD.key(), "id")
            .option(PRECOMBINE_FIELD.key(), "create_time")
            .option(TBL_NAME.key(), "order_detail")
            .save("hdfs:///user/hive/warehouse/ods_ds_hudi.db/order_detail")

    }
}
