package 样卷2Thre.全部计算

import org.apache.spark.sql.types.DataTypes

object yg8js03 {
    def main(args: Array[String]): Unit = {
        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.{SaveMode, SparkSession}

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

        val lianxuliangtiandenglurenshu = order_info
            .select("user_id", "final_total_amount", "create_time")
            .withColumn("create_time", date_sub(col("create_time"), 0))
            .groupBy("user_id", "create_time")
            .agg(sum("final_total_amount") as "totalconsumption", count("*") as "totalorder")
            .withColumn("subDate", row_number().over(win1))
            .withColumn("subCreate_time", col("create_time") - col("subDate"))
            .withColumn("lianxutianshu", count("*").over(win2)) //连续天数
            .where(col("lianxutianshu") === 2)
            .select("user_id")
            .distinct()
            .count()
            .toDouble

        val xiadanrenshu = order_info.select("user_id").distinct().count().toDouble

        val bfb = lianxuliangtiandenglurenshu / xiadanrenshu

        val frame = order_info
            .withColumn("purchaseduser", lit(xiadanrenshu))
            .withColumn("repurchaseduser", lit(lianxuliangtiandenglurenshu))
            .withColumn("repurchaserate", concat(format_number(col("repurchaseduser") / col("purchaseduser"), 1), lit("%")))
            .select("purchaseduser", "repurchaseduser", "repurchaserate")
            .limit(1)
        frame.show
        frame
            .write
            .mode(SaveMode.Overwrite)
            .jdbc(url, "userrepurchasedrate", conf)

    }
}
