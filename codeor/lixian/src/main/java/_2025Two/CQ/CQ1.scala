package _2025Two.CQ


object CQ1 {
    def main(args: Array[String]): Unit = {
        import org.apache.spark.sql.{SaveMode, SparkSession}
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.types.DataTypes
        import org.apache.hudi.config.HoodieWriteConfig._
        import org.apache.hudi.DataSourceWriteOptions._
        import org.apache.hudi.QuickstartUtils._

        import java.util.Properties
        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .appName("hudi")
            .getOrCreate()

        val sql_table_name = "user_info"
        val hudi_table_name = "user_info"
        val pre_combine_field = "operate_time"

        //mysql
        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        val sql_table = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", sql_table_name, conf)

        //hudi
        val hudi = spark.read.format("hudi").load(s"hdfs:///user/hive/warehouse/ods_ds_hudi.db/${hudi_table_name}")
        val hudi_max_time = hudi.select(greatest(col("operate_time"), col("create_time"))).first()(0)

        //updata
        sql_table
            .withColumn("operate_time", coalesce(col("operate_time"),col("create_time")))
            .where(greatest(col("operate_time"),col("create_time")) > hudi_max_time)
            .withColumn("etl_date",lit("20250410"))
            .withColumn("operate_time", col("operate_time").cast(DataTypes.TimestampType))
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
