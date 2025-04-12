package _2025One1.js

object js5 {
    def main(args: Array[String]): Unit = {
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.types.DataTypes
        import org.apache.spark.sql.{SaveMode, SparkSession}

        import java.util.Properties
        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .appName("hudi")
            .getOrCreate()

        val order_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_info")

        val win1 = Window.orderBy("id")
        val win2 = Window.orderBy("diff_value")
        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        order_info
            .orderBy("id")
            .withColumn("add_amount", sum("final_total_amount").over(win1))
            .withColumn("diff_value", abs(col("add_amount") - 2023060600))
            .withColumn("temp_row", row_number().over(win2))
            .orderBy(col("temp_row"))
            .limit(10)
            .select("id", "diff_value")
            .withColumnRenamed("id", "order_id")
            .write
            .mode(SaveMode.Overwrite)
            .jdbc("jdbc:mysql://192.168.45.13:3306/shtd_result?useSSL=false", "order_final_money_amount_diff", conf)


    }
}
