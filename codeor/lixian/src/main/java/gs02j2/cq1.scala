package gs02j2

// spark-sql \
// --conf "spark.sql.extensions=org.apache.spark.sql.hudi.HoodieSparkSessionExtension" \
// --conf "spark.serializer=org.apache.spark.serializer.KryoSerializer" \
object cq1 {
    def main(args: Array[String]): Unit = {
        import org.apache.spark.sql.hudi.HoodieSparkSessionExtension
        import org.apache.spark.serializer.KryoSerializer
        import org.apache.spark.sql.{SaveMode, SparkSession}
        import org.apache.spark.sql.functions._
        import org.apache.hudi.QuickstartUtils._
        import org.apache.hudi.config.HoodieWriteConfig._
        import org.apache.hudi.DataSourceWriteOptions._

        import java.util.Properties
        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .master("local[*]")
            .appName("hudi")
            .getOrCreate()

        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        val sql_table_name = "user_info"
        val hudi_table_name = "user_info"

        val mysql = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", sql_table_name, conf)

        //ods_ds_hudi
        val hudi = spark.read.format("hudi").load(s"hdfs:///user/hive/warehouse/ods_ds_hudi.db/${hudi_table_name}")
        val hudi_max_time = hudi.select(greatest(max("operate_time"), max("create_time"))).first()(0)
        val cols = hudi.columns.map(col)

        //ods增量抽取
        mysql
            .withColumn("operate_time", coalesce(col("operate_time"), col("create_time")))
            .where(greatest(col("operate_time"), col("create_time")) > hudi_max_time)
            .withColumn("etl_date", lit("20250409"))
            .write
            .format("hudi")
            .mode(SaveMode.Append)
            .options(getQuickstartWriteConfigs)
            .option(TBL_NAME.key, hudi_table_name)
            .option(RECORDKEY_FIELD.key, "id")
            .option(PARTITIONPATH_FIELD.key, "etl_date")
            .option(PRECOMBINE_FIELD.key, "operate_time")
            .option(SQL_ENABLE_BULK_INSERT.key,"true")
            .option(HIVE_STYLE_PARTITIONING.key, "true")
            .save(s"hdfs:///user/hive/warehouse/ods_ds_hudi.db/${hudi_table_name}")

    }
}
