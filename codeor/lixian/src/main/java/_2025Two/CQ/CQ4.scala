package _2025Two.CQ

object CQ4 {
    def main(args: Array[String]): Unit = {
        import org.apache.hudi.DataSourceWriteOptions._
        import org.apache.hudi.QuickstartUtils._
        import org.apache.hudi.config.HoodieWriteConfig._
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.types.DataTypes
        import org.apache.spark.sql.{SaveMode, SparkSession}

        import java.util.Properties
        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .appName("hudi")
            .getOrCreate()

        val sql_table_name = "base_region"
        val hudi_table_name = "base_region"
        val pre_combine_field = "create_time"

        //mysql
        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        val sql_table = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", sql_table_name, conf)

        //hudi
        val hudi = spark.read.format("hudi").load(s"hdfs:///user/hive/warehouse/ods_ds_hudi.db/${hudi_table_name}")
        val hudi_max_time = hudi.select(max("id")).first()(0)

        //updata
        sql_table
            .where(col("id") > hudi_max_time)
            .withColumn("create_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
            .withColumn("etl_date",lit("20250410"))
            .withColumn("create_time", col("create_time").cast(DataTypes.TimestampType))
            .write
            .format("hudi")
            .mode(SaveMode.Append)
            .options(getQuickstartWriteConfigs)
            .option(SQL_ENABLE_BULK_INSERT.key,"true")
            .option(HIVE_STYLE_PARTITIONING.key, "true")
            .option(TBL_NAME.key, hudi_table_name)
            .option(RECORDKEY_FIELD.key, "id")
            .option(PRECOMBINE_FIELD.key, pre_combine_field)
            .option(PARTITIONPATH_FIELD.key, "etl_date")
            .save(s"hdfs:///user/hive/warehouse/ods_ds_hudi.db/${hudi_table_name}")




    }
}
