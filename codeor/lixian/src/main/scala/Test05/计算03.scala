package Test05

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions._

object 计算03 {

    def main(args: Array[String]): Unit = {


        System.setProperty("HADOOP_USER_NAME","root")
        val  spark = SparkSession.builder()
            .enableHiveSupport().appName("hive")
            .config("hive.exec.dynamic.partition.mode","nonstrict")
            .master("local[*]")
            .getOrCreate()

        val province = spark.sql("select * from dwd.base_province")
        val region = spark.sql("select * from dwd.dim_region")
        val order_info = spark.sql("select * from dwd.fact_order_info")
        val order_detail = spark.sql("select * from dwd.fact_order_detail")
        val simpleProvince = province.select("id", "name")

        val win1 = Window.partitionBy("user_id").orderBy("day")
        val ttday = order_info
            .withColumn("day", date_sub(col("create_time"), 0))
            .groupBy("user_id", "day")
            .agg(sum("final_total_amount") as "totalconsumption", count("user_id") as "totalorder")
            .select("totalconsumption", "totalorder", "user_id", "day")
            .withColumn("temp", row_number().over(win1))
            .withColumn("tday", date_sub(col("day"), col("temp")))
            .withColumn("连续天数", count(col("tday")).over(Window.partitionBy("user_id", "tday")))

        val _2 = ttday.where(col("连续天数") === 2)
        val _1 = ttday.where(col("user_id") === _2("user_id"))
            .withColumnRenamed("totalconsumption","asd")
            .withColumnRenamed("totalorder","asdasd")
            .withColumnRenamed("day","zxc")
            .withColumnRenamed("user_id","iidd")
            .where(col("连续天数") === 1)
        _2.join(_1,_1("iidd")===_2("user_id") and _1("asd")<_2("totalconsumption"))
            .withColumn("总单量", _1("asdasd")+_2("totalorder"))
            .withColumn("zje",_1("asd")+_2("totalconsumption"))
            .withColumn("qweqwe",concat(_1("zxc"),lit("_"),_2("day")))
            .select(_1("iidd"),col("qweqwe").as("day"),col("zje").as("totalconsumption"),col("总单量").as("totalorder")).show()


//            .withColumn("day")
    }

}
