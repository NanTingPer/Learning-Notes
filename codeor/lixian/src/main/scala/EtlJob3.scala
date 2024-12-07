import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object EtlJob3 {
  def main(args: Array[String]): Unit = {
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
    val DB_URL = "jdbc:mysql://192.168.45.10:3306/ds_db01?useSSL=false" //
    val mysqlProperties = new java.util.Properties()
    mysqlProperties.setProperty("user", "root")
    mysqlProperties.setProperty("password", "123456")
    mysqlProperties.setProperty("driver", "com.mysql.jdbc.Driver")
    val hiveTableData = spark.sql("""
     select * from ads.product_browse;
""")
    hiveTableData.write
      .mode("overwrite") // 或者使用 "overwrite" 来覆盖现有数据
      .jdbc(DB_URL, "online1", mysqlProperties)
  }
}