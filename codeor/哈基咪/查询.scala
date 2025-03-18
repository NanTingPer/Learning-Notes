object 查询 {

def main(args:Array[String]):Unit ={
//    // 创建SparkSession实例
val spark = SparkSession.builder()
    .master("local[*]")
    .appName("ETL Job")
    .config("SPARK_MASTER_HOST", "192.168.45.10")
//      .config("hive.exec.dynamic.partition", "true")//启用Hive的动态分区功能
//      .config("hive.exec.dynamic.partition.mode", "nonstrict")//动态分区模式设置为“非严格”模式
//      .config("spark.sql.sources.partitionOverwriteMode", "dynamic")//设置dynamic模式Spark将只覆盖那些与新数据对应的分区
//      .config("spark.sql.parquet.writeLegacyFormat", "true")//与旧版本的Spark或Hadoop兼
    .enableHiveSupport()//使SparkSession能够使用Hive的功能
    .getOrCreate()//创建一个新的SparkSession实例


//    val sql1 = "select * from order_master where modified_time <= '2022-03-17 23:59:59'"//查询的条件语句
//    val DB_URL = "jdbc:mysql://192.168.45.13:3306/ds_db01?useSSL=false" //数据库连接接口
//    val jdbcMap1 = Map(
//          "url" -> DB_URL, // jdbc url
//          "query" -> sql1, // 查询语句
//          "user" -> "root", // mysql账号
//          "password" -> "123456" // mysql密码
//        )
//    spark.sql("create table order_master_copy as select * from order_master;").show()
    spark.

sql("""
     select * from ads.product_browse limit 10;
""").

show()
  }
      }
