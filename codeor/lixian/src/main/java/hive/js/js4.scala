package hive.js

object js4 {
    def main(args: Array[String]): Unit = {
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.expressions._
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

        val region = spark.table("dwd.dim_region").where(col("etl_date") === "20250407")
        val province = spark.table("dwd.dim_province").where(col("etl_date") === "20250407")
        val order_info = spark.table("dwd.fact_order_info")
        val user_info = spark.table("dwd.dim_user_info").where(col("etl_date") === "20250407")

        val win1 = Window.partitionBy("user_id").orderBy(col("create_time"))
        val win2 = Window.partitionBy("user_id")

        val oneTable = order_info
            .withColumn("create_time", date_sub(col("create_time"), 0))
            .groupBy("user_id", "create_time")
            .agg(count("*") as "user_totalorder", sum("final_total_amount") as "user_totalconsumption")

//        oneTable.show

        val lxDay = oneTable
            .withColumn("dateNum", row_number().over(win1))
            .withColumn("subDate", date_sub(col("create_time"), col("dateNum")))
            .withColumn("lxDay", count("*").over(win2))
            .where(col("lxDay") === 2)
//        lxDay.show
        val lxDay2 = lxDay
            .withColumn("downTotalconsumption", lag(col("user_totalconsumption"), 1).over(win1))
            .withColumn("downCreate_time", lag(col("create_time"), 1).over(win1))
            .withColumn("countOrder", sum("user_totalorder").over(win2))
            .withColumn("sumOrder", sum("user_totalconsumption").over(win2))
            .where(col("downCreate_time").cast(DataTypes.StringType) =!= "null")
            .where(col("user_totalconsumption") > col("downTotalconsumption"))
            .withColumn("day", concat(date_format(col("downCreate_time"), "yyyyMMdd"), lit("_"), date_format(col("create_time"), "yyyyMMdd")))
            .join(user_info, col("user_id") === user_info("id"))
            .select("user_id", "name", "day", "sumOrder", "countOrder")
            .withColumnRenamed("user_id", "userid")
            .withColumnRenamed("name", "username")
            .withColumnRenamed("sumOrder", "totalconsumption")
            .withColumnRenamed("countOrder", "totalorder")
            .withColumn("totalconsumption", col("totalconsumption").cast(DataTypes.DoubleType))

        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        lxDay2
            .write
            .mode(SaveMode.Overwrite)
            .jdbc("jdbc:mysql://192.168.45.13:3306/shtd_result?useSSL=false", "usercontinueorder", conf)
    }
}
