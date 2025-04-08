package hive.js.y7

import java.util.Properties

object js3 {
    def main(args: Array[String]): Unit = {
        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.{SaveMode, SparkSession}
        val spark = SparkSession
            .builder()
            .config("hive.exec.dynamic.partition.mode","nonstrict")
            .master("local[*]")
            .appName("hive")
            .enableHiveSupport()
            .getOrCreate()

        val region = spark.table("dwd.dim_region").where(col("etl_date") === "20250407")
        val province = spark.table("dwd.dim_province").where(col("etl_date") === "20250407")
        val order_info = spark.table("dwd.fact_order_info")

        val win1 = Window.partitionBy("user_id").orderBy(col("create_time"))
        val win2 = Window.partitionBy("user_id", "subDate")
        //连续两天
        val twoDay = order_info
            .select("user_id", "create_time")
            .withColumn("create_time", date_sub(col("create_time"), 0))
            .distinct()
            .withColumn("num", row_number().over(win1))
            .withColumn("subDate", date_sub(col("create_time"), col("num")))
            .withColumn("lxDay", count("*").over(win2))
            .where(col("lxDay") === 2)
            .select("user_id")
            .distinct()
            .count()

        //下单人数
        val ren = order_info.select("user_id").distinct().count()

        val fin = order_info.limit(1)
            .withColumn("purchaseduser", lit(ren))
            .withColumn("repurchaseduser", lit(twoDay))
            .withColumn("repurchaserate", concat(round(col("repurchaseduser") / col("purchaseduser"), 1), lit("%")))
            .select("purchaseduser", "repurchaseduser", "repurchaserate")
        fin.show
        val conf = new Properties()
        conf.put("user", "root")
        conf.put("password", "123456")
        fin
            .write
            .mode(SaveMode.Overwrite)
            .jdbc("jdbc:mysql://192.168.45.13:3306/shtd_result?useSSL=false", "userrepurchasedrate", conf)
    }
}
