package Test04

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions._
import org.apache.spark.sql.functions._

import java.util.Properties

object 计算04第二次 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME", "root")
        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .appName("hive")
            .master("local[*]")
            .getOrCreate()


        val order_info = spark.sql("select * from dwd.fact_order_info")
        val user_info = spark.sql("select * from dwd.dim_user_info")
        val win1 = Window.partitionBy("user_id").orderBy("create_time")
//val ordersDF = order_info
//    .withColumn("order_date", date_format(col("create_time"), "yyyy-MM-dd"))
//    .groupBy("user_id", "order_date")
//    .agg(
//        sum("final_total_amount").alias("total_amount"),
//        count("user_id").alias("order_count")
//    )
//    .orderBy("user_id", "order_date").show
        val win2 = Window.partitionBy("user_id", "tempDate").orderBy("tempDate")
        val dayMax2 = order_info
            .withColumn("tempNum", row_number().over(win1))
            .withColumn("create_time", date_sub(col("create_time"), 0))
            .withColumn("tempDate", date_sub(col("create_time"), col("tempNum")))
            .withColumn("连续天数", count("tempDate").over(win2))
            .select("user_id", "create_time", "tempDate","连续天数")
            .where(col("连续天数") === 2)
            .withColumnRenamed("create_time", "time")
        dayMax2.orderBy("user_id").show()

        val win3 = Window.partitionBy("user_id", "create_time")/*.orderBy("create_time")*/
        val 总金额 = order_info.where(col("user_id") === dayMax2.col("user_id"))
            .orderBy("user_id")
            .select("user_id", "final_total_amount", "create_time")
            .withColumn("create_time", date_sub(col("create_time"), 0))
            .withColumn("单量", count("*").over(Window.partitionBy("user_id")))//
            .withColumn("金额", sum(col("final_total_amount")).over(win3))
            .drop("final_total_amount")
            .distinct()
            .withColumnRenamed("user_id", "order_user_id")
            .withColumnRenamed("create_time", "order_create_time")
        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        val win4 = Window.partitionBy("user_id").orderBy("time")
        dayMax2.join(总金额, 总金额.col("order_user_id") === dayMax2.col("user_id") and (dayMax2.col("time") === 总金额.col("order_create_time")))
            .orderBy("user_id","time")
            .withColumn("lead", lead(col("金额"), 1).over(win4))//把下一条的金额提出来
            .withColumn("两天总金额", sum("金额").over(win4))
            .withColumn("两天总金额", max(col("两天总金额")).over(Window.partitionBy("user_id")))
            .where(col("金额") - col("lead") > 0)
            .orderBy("user_id")
            .withColumn("day", concat(col("time"), lit("_"),date_add(col("time"), 1)))
            .drop("连续天数", "order_user_id", "lead","金额", "tempDate", "time")
            .join(user_info, user_info.col("id") === col("user_id"))
            .select("user_id", "name", "day", "两天总金额", "单量")
            .orderBy("user_id")
            .select(col("user_id").as("userid"),col("name").as("username"),col("day"),col("两天总金额").as("totalconsumption"),col("单量").as("totalorder"))
            .distinct()
            .write
            .jdbc("jdbc:mysql://192.168.45.13/shtd_result?useSSL=false", "usercontinueorder",conf)



    }
}
