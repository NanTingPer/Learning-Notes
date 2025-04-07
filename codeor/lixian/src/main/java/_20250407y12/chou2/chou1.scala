package _20250407y12.chou2

object chou1 {
    //spark-sql \
    //--conf "spark.sql.extensions=org.apache.spark.sql.hudi.HoodieSparkSessionExtension" \
    //--conf "spark.serializer=org.apache.spark.serializer.KryoSerializer" \
    //--jars /hudi-spark3.1-bundle_2.12-0.12.0.jar
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.{SaveMode, SparkSession}
        import org.apache.spark.sql.hudi.HoodieSparkSessionExtension
        import org.apache.spark.serializer.KryoSerializer
        import org.apache.hudi.config.HoodieWriteConfig._ //TBLNAME
        import org.apache.hudi.QuickstartUtils._
        import org.apache.hudi.DataSourceWriteOptions._
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
        val mysql = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "user_info", conf)

        //ods最大
        val odstable = spark.read.load("hdfs:///user/hive/warehouse/ods_ds_hudi.db/user_info")
        val odsmaxTime = odstable.select(greatest(max("operate_time"), max("create_time"))).first()(0)

        //mysql增量
        mysql
          .where(greatest(col("operate_time"), col("create_time")) > odsmaxTime)
            .withColumn("etl_date", lit("20250406"))
          .withColumn("operate_time", coalesce(col("operate_time"), col("create_time")))
            .write
            .mode(SaveMode.Append)
            .format("hudi")
            .options(getQuickstartWriteConfigs)
            .option(SQL_ENABLE_BULK_INSERT.key(), "true")
            .option(HIVE_STYLE_PARTITIONING.key(), "true")
            .option(PARTITIONPATH_FIELD.key(), "etl_date")
            .option(RECORDKEY_FIELD.key(), "id")
            .option(PRECOMBINE_FIELD.key(), "operate_time")
            .option(TBL_NAME.key(), "user_info")
            .save("hdfs:///user/hive/warehouse/ods_ds_hudi.db/user_info")

    }
}
