object EtlJob4 {

def main(args:Array[String]):Unit ={
//    // 创建SparkSession实例
val spark = SparkSession.builder()
    .master("local[*]")
    .appName("ETL Job")
    .config("hive.exec.dynamic.partition", "true")
    .config("hive.exec.dynamic.partition.mode", "nonstrict")
    .config("spark.sql.sources.partitionOverwriteMode", "dynamic")
    .config("spark.sql.parquet.writeLegacyFormat", "true")
    .enableHiveSupport()
    .getOrCreate()
val DB_URL = "jdbc:mysql://192.168.45.13:3306/ds_db01?useSSL=false" //
val stock_coupon_use_sql = "select * from coupon_use where pay_time <'20220610122389' and get_time<'20220610122389' and used_time <'20220610122389'"
val jdbcMap = Map(
    "url" ->DB_URL, // jdbc url
    "query"->stock_coupon_use_sql, // 查询语句
    "user"->"root", // mysql账号
    "password"->"123456" // mysql密码
    )
val df = spark.read.format("jdbc").options(jdbcMap).load()
val stock_coupon_use_df = df.withColumn("etl_date", lit("20231225"))
    stock_coupon_use_df.write
        .

format("parquet")
      .

mode("overwrite") // 覆盖
      .

partitionBy("etl_date") // 指定分区
      .

saveAsTable("ods.coupon_use") // 用的是自己命名的ods名称
    spark.

table("ods.coupon_use")
    spark.

sql("select greatest(max(get_time),max(used_time),max(pay_time)) from ods.coupon_use where get_time != 'NULL' and used_time !='NULL' and pay_time != 'NULL'").

show()
  }
      }
