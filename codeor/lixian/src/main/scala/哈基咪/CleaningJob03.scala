object CleaningJob03 {

def main(arg:Array[String]):Unit ={
val spark = SparkSession.builder()
    .master("local[*]")
    .appName("CLEAN Job")
    .config("hive.exec.dynamic.partition", "true")
    .config("hive.exec.dynamic.partition.mode", "nonstrict")
    .config("spark.sql.sources.partitionOverwriteMode", "dynamic")
    .config("spark.sql.parquet.writeLegacyFormat", "true")
    .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    .enableHiveSupport()
    .getOrCreate()
val test_df = spark.sql("select * from ods.order_master")
    .withColumn("create_time", substring(col("create_time"), 1, 8))
    .withColumn("dwd_insert_user", lit("user"))
    .withColumn("dwd_insert_time", current_timestamp())
    .withColumn("dwd_modify_user", lit("user"))
    .withColumn("dwd_modify_time", current_timestamp())

    test_df.

show()
  }
      }
