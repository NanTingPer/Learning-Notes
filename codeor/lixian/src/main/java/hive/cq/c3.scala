package hive.cq


object c3 {
    def main(args: Array[String]): Unit = {

        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.types.DataTypes
        import org.apache.spark.sql.{SaveMode, SparkSession}

        import java.util.Properties
        val spark = SparkSession
            .builder()
            .config("hive.exec.dynamic.partition.mode","nonstrict")
            .master("local[*]")
            .appName("hive")
            .enableHiveSupport()
            .getOrCreate()

        val conf = new Properties()
        conf.put("user","root")
        conf.put("password", "123456")
        //mysql
        val mysql = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "base_province", conf)

        //ods
        val odsuser_info = spark.table("ods.base_province")
        val hiveMaxTime = odsuser_info.select(max("id")).first()(0)
        val cols = odsuser_info.columns.map(col)
        //updata
        mysql
            .where(col("id") > hiveMaxTime)
            .withColumn("create_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
            .withColumn("etl_date", lit("20250407"))
            .select(cols:_*)
            .write
            .mode(SaveMode.Append)
            .format("hive")
            .partitionBy("etl_date")
            .saveAsTable("ods.base_province")

    }
}
