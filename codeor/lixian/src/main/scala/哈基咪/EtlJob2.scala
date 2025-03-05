object EtlJob2 {

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
val stock_product_info_sql =
    """
        |SELECT
        |  concat( DATE_FORMAT( NOW( ), '%Y-%m-%d %H:%i:%s' ),'-',product_id ) AS row_key,
        |  product_id,
        |  count( DISTINCT customer_id ) AS uv,
        |  count( * ) AS pv
        |FROM product_browse
        |GROUP BY
        |  product_id
        |""".stripMargin
val jdbcMap = Map(
    "url" ->DB_URL, // jdbc url
    "query"->stock_product_info_sql, // 查询语句
    "user"->"root", // mysql账号
    "password"->"123456" // mysql密码
    )
val df = spark.read.format("jdbc").options(jdbcMap).load()
val stock_product_info_df = df.withColumn("etl_date", lit("20231225"))
    stock_product_info_df.write
        .

format("parquet")
      .

mode("overwrite") // 覆盖
      .

partitionBy("etl_date") // 指定分区
      .

saveAsTable("ads.product_browse") // 用的是自己命名的ods名称

  }
      }
