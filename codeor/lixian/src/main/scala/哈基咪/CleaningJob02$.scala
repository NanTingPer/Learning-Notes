object CleaningJob02 {

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
val dim_product_info = spark.sql("select * from dwd.dim_product_info where etl_date='20231225'")
val columns = dim_product_info.columns.map(col(_))
val new_product_info = spark
    .sql("select * from ods.product_info where etl_date='20231225'")
    .withColumn("dwd_insert_user", lit("user1"))
    .withColumn("dwd_insert_time", date_trunc("second", current_timestamp()))
    .withColumn("dwd_modify_user", lit("user1"))
    .withColumn("dwd_modify_time", date_trunc("second", current_timestamp()))
    .select(columns:_*)
val union_df = dim_product_info.union(new_product_info)
val w1 = Window.partitionBy("product_id").orderBy(col("modified_time").desc)
val w2 = Window.partitionBy("product_id")
val union_end_df = union_df
    .withColumn("row_number", row_number().over(w1))
    .withColumn("dwd_insert_time", min("dwd_insert_time").over(w2))
    .withColumn("dwd_modify_time", max("dwd_modify_time").over(w2))
val merge_df = union_end_df.where("row_number=1").drop("row_number")
    merge_df
        .write
        .

mode("overwrite")
      .

saveAsTable("tmp_dim_product_info")
    spark
        .

table("tmp_dim_product_info")
      .write
          .

mode("overwrite")
      .

insertInto("dwd.dim_product_info")
    spark.

sql("drop table tmp_dim_product_info")
  }
      }
