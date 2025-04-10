package _2025One1.cq

//spark-shell \
//--conf "spark.sql.extensions=org.apache.spark.sql.hudi.HoodieSparkSessionExtension" \
//--conf "spark.serializer=org.apache.spark.serializer.KryoSerializer" \
//--jars

object cq4 {
    def main(args: Array[String]): Unit = {
        import org.apache.hudi.DataSourceWriteOptions._
        import org.apache.hudi.QuickstartUtils._
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.types.DataTypes
        import org.apache.spark.sql.{SaveMode, SparkSession}

        import java.util.Properties

        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .appName("hudi")
            .getOrCreate()

        val mysql_table = "base_region"
        val hudi_table = "base_region"
        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        val mysql = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", mysql_table, conf)

        val hudi = spark.read.format("hudi").load(s"hdfs:///user/hive/warehouse/ods_ds_hudi.db/${hudi_table}")
        val hudi_max_time = hudi.select(max("id")).first()(0)

        //up data
        mysql
            //null
            .where(col("id") > hudi_max_time)
            .withColumn("create_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
            .withColumn("etl_date", lit("20250409"))
            .write
            .format("hudi")
            .mode(SaveMode.Append)
            .options(getQuickstartWriteConfigs)
            .option(SQL_ENABLE_BULK_INSERT.key, "true")
            .option(HIVE_STYLE_PARTITIONING.key, "true")
            .option(RECORDKEY_FIELD.key, "id")
            .option(PARTITIONPATH_FIELD.key, "etl_date")
            .option(PRECOMBINE_FIELD.key, "create_time")
            .save(s"hdfs:///user/hive/warehouse/ods_ds_hudi.db/${hudi_table}")
    }
}
