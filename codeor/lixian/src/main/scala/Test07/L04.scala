package Test07

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.api.java.UDF2
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions._
import org.apache.spark.sql.types.DataTypes

import java.util.Properties

object L04 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession.builder()
            .appName("hive")
            .enableHiveSupport()
            .config("hive.exec.dynamic.partition.mode","nonstrict")
            .master("local[*]")
            .getOrCreate()

        val province = spark.sql("select * from dwd.dim_base_province")
        val order_info = spark.sql("select * from dwd.fact_order_info")
        val region = spark.sql("select * from dwd.dim_region")
        val user_info = spark.sql("select * from dwd.dim_user_info")

        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")


        val aggg = order_info.withColumn("create_time", date_add(col("create_time"), 0))
            .groupBy("user_id", "create_time")
            .agg(sum("final_total_amount") as "totalconsumption", count("*") as "totalorder")


        val win1 = Window.partitionBy("user_id").orderBy("create_time")
        val agggg = aggg
            .withColumn("tempnum", row_number().over(win1))
            .withColumn("tempDate", date_sub(col("create_time"), col("tempnum")))
            .withColumn("counttempDate", count("*").over(Window.partitionBy("user_id", "tempDate")))
            .drop("tempnum", "tempDate")
            .where(col("counttempDate") === 2)

        val aggggg = agggg
            .withColumn("downCreateTime", lead(col("create_time"), 1).over(Window.partitionBy("user_id").orderBy("create_time")))
            .withColumn("downTotalconsumption", lead(col("totalconsumption"), 1).over(Window.partitionBy("user_id").orderBy("create_time")))
            .withColumn("downTotalorder", lead(col("totalorder"), 1).over(Window.partitionBy("user_id").orderBy("create_time")))
            .where(col("totalconsumption") < col("downTotalconsumption"))
//                        .show
            .withColumn("day", concat(col("create_time"), lit("_"), col("downCreateTime")))
            .withColumn("totalconsumption", col("totalconsumption") + col("downTotalconsumption"))
            .withColumn("totalorder", col("totalorder") + col("downTotalorder"))
            .select("user_id", "day", "totalconsumption", "totalorder")

        aggggg.join(user_info, col("user_id") === user_info("id"))
            .select(col("user_id") as "userid",
                col("name") as "username",
                col("day"),
                col("totalconsumption"),
                col("totalorder"))
            .write
            .jdbc("jdbc:mysql://192.168.45.13:3306/shtd_result?useSSL=false", "usercontinueorder", conf)
    }

}
