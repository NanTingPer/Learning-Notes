package 样卷2Thre.全部计算

object yg1js04 {
    def main(args: Array[String]): Unit = {
        import org.apache.spark.sql.{SaveMode, SparkSession}
        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.functions._

        import java.util.Properties
        val spark = SparkSession.builder()
            .enableHiveSupport()
            .appName("wfawfawf")
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
            .getOrCreate()

        val conf: Properties = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        val url = "jdbc:mysql://192.168.45.13:3306/shtd_result?useSSL=false"
        val order_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_info")
        val province = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_province").where(col("etl_date") === "20250326")
        val region = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_region").where(col("etl_date") === "20250326")
        val user_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/ods_ds_hudi.db/user_info")
        user_info.show

        val win1 = Window.partitionBy("user_id").orderBy("create_time")
        val win2 = Window.partitionBy("user_id","subCreate_time")
        val win3 = Window.partitionBy("user_id").orderBy("create_time")
        val win4 = Window.partitionBy("user_id")

        order_info
            .select("user_id","final_total_amount","create_time")
            .withColumn("create_time", date_sub(col("create_time"), 0))
            .groupBy("user_id", "create_time")
            .agg(sum("final_total_amount") as "totalconsumption", count("*") as "totalorder")
            .withColumn("subDate", row_number().over(win1))
            .withColumn("subCreate_time", col("create_time") - col("subDate"))
            .withColumn("lianxutianshu", count("*").over(win2)) //连续天数
            .where(col("lianxutianshu") === 2)
            .orderBy("user_id")
            .withColumn("downTotalconsumption", lead("totalconsumption", 1).over(win3))
            .withColumn("downCreate_time", lead("create_time", 1).over(win3))
            .withColumn("ttotalconsumption", sum("totalconsumption").over(win4))
            .withColumn("ttotalorder", sum("totalorder").over(win4))
            .where(col("downTotalconsumption") > col("totalconsumption"))
            .drop("totalconsumption", "totalorder")
            .withColumnRenamed("ttotalconsumption","totalconsumption")
            .withColumnRenamed("ttotalorder","totalorder")
            .withColumn("day", concat(col("create_time"), lit("_"), col("downCreate_time")))
            .drop("create_time","downCreate_time")
            .join(user_info, col("user_id") === user_info("id"))
            .select("user_id", "name", "day", "totalconsumption", "totalorder")
            .withColumnRenamed("user_id", "userid")
            .withColumnRenamed("name", "username")
            .write
            .mode(SaveMode.Overwrite)
            .jdbc(url, "usercontinueorder", conf)

    }
}
