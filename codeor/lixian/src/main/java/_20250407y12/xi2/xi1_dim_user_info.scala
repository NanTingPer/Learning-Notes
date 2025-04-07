package _20250407y12.xi2


object xi1_dim_user_info {
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
        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.types.DataTypes
        import org.apache.spark.sql.{SaveMode, SparkSession}

        val spark = SparkSession
            .builder()
            .appName("hive")
          .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
          .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
          .enableHiveSupport()
            .master("local[*]")
            .getOrCreate()

        //ods
        val odstable = spark.read.load("hdfs:///user/hive/warehouse/ods_ds_hudi.db/user_info").where(col("etl_date") === "20250406")
          .drop("_hoodie_commit_time", "_hoodie_commit_seqno", "_hoodie_record_key", "_hoodie_partition_path", "_hoodie_file_name")
        //dwd
        val dwdtable = spark.read.load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_user_info")
        val maxpar = dwdtable.select(max(col("etl_date"))).first()(0)
        val newdwdtable = dwdtable.where(col("etl_date") === maxpar)
          .drop("_hoodie_commit_time", "_hoodie_commit_seqno", "_hoodie_record_key", "_hoodie_partition_path", "_hoodie_file_name")
        val cols = newdwdtable.columns.map(col)

        //ods插
        val unionTable = odstable
            .withColumn("dwd_insert_user", lit("user1"))
            .withColumn("dwd_insert_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
            .withColumn("dwd_modify_user", lit("user1"))
            .withColumn("dwd_modify_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
            .select(cols: _*)
            .union(newdwdtable)

        //合并修改
        val win1 = Window.partitionBy("id").orderBy(col("operate_time").desc)
        val win2 = Window.partitionBy("id")

        unionTable
            .withColumn("temp", row_number().over(win1))
            .withColumn("dwd_modify_time", max(col("dwd_modify_time")).over(win2))
            .withColumn("dwd_insert_time", min(col("dwd_insert_time")).over(win2))
            .where(col("temp") === 1)
            .drop("temp")
            .withColumn("etl_date", lit("20250406"))
          .withColumn("operate_time", col("operate_time").cast(DataTypes.TimestampType))
          .write
          .mode(SaveMode.Append)
          .format("hudi")
          .options(getQuickstartWriteConfigs)
          .option(SQL_ENABLE_BULK_INSERT.key(), "true")
          .option(HIVE_STYLE_PARTITIONING.key(), "true")
          .option(PARTITIONPATH_FIELD.key(), "etl_date")
          .option(RECORDKEY_FIELD.key(), "id")
          .option(PRECOMBINE_FIELD.key(), "operate_time")
          .option(TBL_NAME.key(), "dim_user_info")
          .save("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_user_info")

    }
}
