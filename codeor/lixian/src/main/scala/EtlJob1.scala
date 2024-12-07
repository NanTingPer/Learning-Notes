import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object EtlJob1 {
  def main(args: Array[String]): Unit = {
    //    // 创建SparkSession实例
    val spark = SparkSession.builder()
      .master("local[*]")
      .appName("ETL Job")
      .config("hive.exec.dynamic.partition", "true")//启用Hive的动态分区功能
      .config("hive.exec.dynamic.partition.mode", "nonstrict")//动态分区模式设置为“非严格”模式
      .config("spark.sql.sources.partitionOverwriteMode", "dynamic")//设置dynamic模式Spark将只覆盖那些与新数据对应的分区
      .config("spark.sql.parquet.writeLegacyFormat", "true")//与旧版本的Spark或Hadoop兼
      .enableHiveSupport()//使SparkSession能够使用Hive的功能
      .getOrCreate()//创建一个新的SparkSession实例


    val sql1 = "select * from order_detail where modified_time <= '2022-03-17 23:59:59'"//查询的条件语句
    val DB_URL = "jdbc:mysql://192.168.45.13:3306/ds_db01?useSSL=false" //数据库连接接口
    val jdbcMap1 = Map(
          "url" -> DB_URL, // jdbc url
          "query" -> sql1, // 查询语句
          "user" -> "root", // mysql账号
          "password" -> "123456" // mysql密码
        )
    val df1 = spark.read.format("jdbc").options(jdbcMap1).load()//从JDBC数据源读取数据到DataFrame
    val customer_df1 = df1.withColumn("etl_date", lit("20221123"))
    customer_df1.write
      .format("parquet")
      .mode("overwrite") // 覆盖
      .partitionBy("etl_date") // 指定分区
      .saveAsTable("ods.order_detail") // 用的是自己命名的ods名称
//    spark.table("ods.order_detail").show()
//
    val max_time_sql = "select max(modified_time) from ods.order_detail"
    val df_get = spark.sql(max_time_sql)
    val max_time = df_get.first.getAs[java.sql.Timestamp](0)// 2022-03-17 23:32:18.0
    df_get.show
    println(max_time)
    val sql2 = s"select * from order_detail where modified_time > '${max_time}'"
    val jdbcMap2 = Map(
      "url" -> DB_URL, // jdbc url
      "query" -> sql2, // 查询语句
      "user" -> "root", // mysql账号
      "password" -> "123456" // mysql密码
    )
    val df = spark.read.format("jdbc").options(jdbcMap2).load()

    // ---------- 2. Transfor 转换-增加静态分区列 -------------
    val df2 = df.withColumn("etl_date",lit("20231123"))
    df2.write
      .format("parquet")
      .mode("append")          // 覆盖
      .partitionBy("etl_date")    // 指定分区
      .saveAsTable("ods.order_detail")         // 用的是自己命名的ods名称
    // saveAsTable()方法：会将DataFrame数据保存到Hive表中
    spark.table("ods.order_detail").show()
    spark.sql("show partitions ods.order_detail").show()
  }
}