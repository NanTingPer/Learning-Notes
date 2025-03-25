package hudi.Temp

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.hudi.DataSourceWriteOptions._
import org.apache.hudi.QuickstartUtils.getQuickstartWriteConfigs
import org.apache.hudi.config.HoodieWriteConfig

import java.util.Properties

object Write {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")

        val spark = SparkSession
            .builder()
            .master("local[*]")
            .appName("temp")
            .enableHiveSupport()
            .config("spark.serializer","org.apache.spark.serializer.KryoSerializer")
            .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
            .getOrCreate()

        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        val mysqlTable = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "user_info", conf)
            .withColumn("operate_time", coalesce(col("operate_time"), col("create_time")))
        mysqlTable.createTempView("temptable")
        spark.sql("select * from temptable limit 5").show()
        val mysqltwo = mysqlTable.withColumn("etl_date",lit("20250324"))
        mysqltwo.write
            .format("hudi")
            .options(getQuickstartWriteConfigs)
            .option(RECORDKEY_FIELD.key, "id") //Key
            .option(PARTITIONPATH_FIELD.key, "etl_date") //分区字段
            .option(PRECOMBINE_FIELD.key,"operate_time") //聚合字段
            .option(HoodieWriteConfig.TBL_NAME.key, "user_info") //表名
            .mode(SaveMode.Append)
            .save("hdfs:///user/hive/warehouse/ods_ds_hudi.db/user_info")
    }
}
