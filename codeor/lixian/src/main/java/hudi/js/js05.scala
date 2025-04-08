package hudi.js

object js05 {
    def main(args: Array[String]): Unit = {
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.expressions._
        import java.util.Properties
        import org.apache.spark.sql.types.DataTypes
        import org.apache.spark.sql.{SaveMode, SparkSession}
        val spark = SparkSession
            .builder()
            .config("hive.exec.dynamic.partition.mode","nonstrict")
            .master("local[*]")
            .appName("hive")
            .enableHiveSupport()
            .getOrCreate()

        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        val order_info = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "order_info", conf)


        val win1 = Window.orderBy("id")
        order_info
            .orderBy(col("final_total_amount"))
            .select("final_total_amount", "id")
            .withColumn("value", sum(col("final_total_amount")).over(win1))
            .withColumn("diff_value", col("value") - 2023060600)
            .withColumn("absDiff_value", abs(col("diff_value")))
            .withColumn("pm", row_number().over(Window.orderBy("absDiff_value")))
            .orderBy("pm")
            .limit(10)
            .select("id", "diff_value")
            .withColumnRenamed("id", "order_id")
            .withColumn("diff_value", col("diff_value").cast(DataTypes.DoubleType))
            .write
            .mode(SaveMode.Overwrite)
            .jdbc("jdbc:mysql://192.168.45.13:3306/shtd_result?useSSL=false", "order_final_money_amount_diff", conf)

    }
}
