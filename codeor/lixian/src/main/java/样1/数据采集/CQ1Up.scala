package 样1.数据采集
object CQ1Up {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        import org.apache.spark.sql.{SaveMode, SparkSession}
        import org.apache.spark.sql.functions._

        import java.util.Properties
        //todo 获取spark
        val spark = SparkSession
            .builder()
            .appName("hive")
            .enableHiveSupport()
            .master("local[*]")
            .getOrCreate()

        //todo 获取mysql数据
        val conf = new Properties();conf.put("user","root");conf.put("password","123456")
        val jdbcUri = "jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false"
        val mysql = spark.read.jdbc(jdbcUri, "user_info", conf)

        //todo 获取hive存量 增量时间
        val hive = spark.sql("select * from ods.user_info")
        val maxtime = hive.select(greatest(col("operate_time"), col("create_time")))
        val cols = hive.columns.map(col)

        //todo 取出mysql增量
        mysql
            .where(greatest(col("operate_time"),col("create_time")) > maxtime)
            .withColumn("etl_date", lit("20250330"))
            .select(cols:_*)
            .write
            .mode(SaveMode.Append)
            .format("hive")
            .partitionBy("etl_date")
            .saveAsTable("ods.user_info")

    }
}
