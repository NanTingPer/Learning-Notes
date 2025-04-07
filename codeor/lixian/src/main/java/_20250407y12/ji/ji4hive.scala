package _20250407y12.ji

import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions._
import org.apache.spark.sql.types.DataTypes
import org.apache.spark.sql.{SaveMode, SparkSession}

import java.util.Properties

object ji4hive {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession
            .builder()
            .appName("hive")
            .config("hive.exec.dynamic.partition.mode","nonstrict")
            .enableHiveSupport()
            .master("local[*]")
            .getOrCreate()

        val region = spark.table("dwd.dim_region").where(col("etl_date") === "20250406")
        val province = spark.table("dwd.dim_province").where(col("etl_date") === "20250406")
        val order_info = spark.table("dwd.fact_order_info")
        val user_info = spark.table("dwd.dim_user_info").where(col("etl_date") === "20250406")

        val win1 = Window.partitionBy("user_id").orderBy("create_time")
        val win2 = Window.partitionBy("user_id", "subDate")
        val win3 = Window.partitionBy("user_id", "subDate").orderBy("create_time")

        val OnePars = order_info
            .withColumn("create_time", date_sub(col("create_time"), 0))
            .groupBy("user_id", "create_time")
            .agg(count(col("*")) as "totalorder", sum("final_total_amount") as "totalconsumption")

        val conf = new Properties()
        conf.put("user","root")
        conf.put("password", "123456")
        OnePars
            .withColumn("subNum", row_number().over(win1))
            .withColumn("subDate", date_sub(col("create_time"), col("subNum")))
            .withColumn("sunDay", count("*").over(win2))
            .orderBy("user_id")
            .where(col("sunDay") === 2)
          .withColumn("Twototalconsumption", sum(col("totalconsumption")).over(win2))
          .withColumn("Twototalorder", sum(col("totalorder")).over(win2))
          .withColumn("downtotalconsumption", lag("totalconsumption", 1).over(win3)) //上一天的金额
          .withColumn("downcreate_time", lag("create_time", 1).over(win3)) //上一天的时间
          .where(col("downtotalconsumption").cast(DataTypes.StringType) =!= lit("null"))
          .where(col("Twototalconsumption") > col("totalconsumption"))//26比25高
          .withColumn("day", concat(date_format(col("downcreate_time"), "yyyyMMdd"), lit("_"), date_format(col("create_time"), "yyyyMMdd")))
          .select("user_id", "day", "Twototalconsumption", "Twototalorder")
          .withColumnRenamed("Twototalconsumption", "totalconsumption")
          .withColumnRenamed("Twototalorder", "totalorder")
          .join(user_info, col("user_id") === user_info("id"))
          .select("user_id", "name", "day", "totalconsumption", "totalorder")
          .withColumnRenamed("user_id", "userid")
          .write
          .jdbc("jdbc:mysql://192.168.45.13:3306/shtd_result?useSSL=false", "usercontinueorder", conf)


//        val conf = new Properties()
//        conf.put("password", "123456")
//        conf.put("user", "root")
//            .write
//            .mode(SaveMode.Overwrite)
//            .jdbc("jdbc:mysql://192.168.45.13:3306/shtd_result?useSSL=false", "provinceavgcmp", conf)
    }


}
