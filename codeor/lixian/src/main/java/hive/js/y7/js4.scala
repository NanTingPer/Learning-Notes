package hive.js.y7

import java.util.Properties

object js4 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
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

        val table = order_info
            .groupBy("province_id")
            .agg(count("*") as "Amount")
            .join(province, province("id") === col("province_id"))
            .select("name", "Amount")
            .orderBy(col("Amount").desc)

        val RowList = table.collect()
        var varTable = order_info.limit(1).select("user_id")
        RowList.foreach(f => varTable = varTable.withColumn(f.get(0).toString, lit(f.get(1))))
        varTable.drop("user_id").show
    }
}
