package Test04

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.{col, concat, count, date_add, date_format, date_sub, lit, row_number, sum}
import org.apache.spark.sql.types.DataTypes

object 计算04 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME", "root")
        val spark = SparkSession.builder()
            .master("local[*]")
            .enableHiveSupport()
            .appName("hive")
            .getOrCreate()

        val dim_user_info = spark.sql("select * from dwd.dim_user_info").withColumnRenamed("name","user_name")
        val fact_order_info = spark.sql("select * from dwd.fact_order_info")



//        val OnePrTable = dim_user_info.join(fact_order_info, dim_user_info.col("id") === fact_order_info("user_id")).select("user_id", "user_name", "fact_order_info.create_time", "final_total_amount").withColumnRenamed("fact_order_info.create_time", "create_time")
//        OnePrTable.createTempView("onePrTable")



//        val win1 = Window.partitionBy("user_id").orderBy("create_time")
//        val win2 = Window.partitionBy("user_id", "tempTime").orderBy("tempTime")
//        val win3 = Window.partitionBy("user_id");
//        val timeAndUser = OnePrTable.withColumn("tempCol", row_number().over(win1))
//            .withColumn("tempTime", date_sub(col("create_time"), col("tempCol"))) //tempTime => 如果同一个user有两个相同 就是连续两天
//            .drop("tempCol")
//            .distinct()
//            .withColumn("连续天数", row_number().over(win2))
//            .where((col("连续天数") === 2) or (date_sub(col("create_time"), 2) === col("tempTime")))
//            .withColumn("总金额", sum(col("final_total_amount")).over(win3))
//            .withColumn("总单数", count("*").over(win3))
//            .where((col("连续天数") === 2) and (col("总单数") >= 2))
//            .distinct()
//            .withColumn("day", concat(date_add(col("tempTime"), 1).cast(DataTypes.StringType), lit("_"), date_add(col("tempTime"), 2).cast(DataTypes.StringType)))
//            .show()

//        timeAndUser.createTempView("timeAndUser")

//        spark.sql("select * from onePrTable").show()
//        spark.sql("select * from timeAndUser").show()


//        val win3 = Window.partitionBy("user_id")
//        val 总金额和单数 = OnePrTable/*.join(timeAndUser, timeAndUser.col("user_id") === OnePrTable.col("user_id"))*/
//            .where(
//                (date_add(OnePrTable.col("create_time"), 0) === date_add(timeAndUser.col("tempTime"), 1))
//                or
//                (date_add(OnePrTable.col("create_time"), 0) === date_add(timeAndUser.col("tempTime"), 2))
//            )
//            .drop("timeAndUser.create_time", "timeAndUser.final_total_amount", "timeAndUser.user_id", "timeAndUser.user_name")

//            .withColumn("总金额", sum(col("final_total_amount").cast(DataTypes.DoubleType)).over(win3))
//            .withColumn("单数", count("*").over(win3)).show



    }
}
