package _2025Two.JS

object JS4 {
    def main(args: Array[String]): Unit = {
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.types.DataTypes
        import org.apache.spark.sql.{SaveMode, SparkSession}

        import java.util.Properties
        val spark = SparkSession
            .builder()
            .enableHiveSupport()
//            .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
//            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .config("spark.sql.debug.maxToStringFields", "99999")
            .appName("hudi")
            .getOrCreate()

        val conf = new Properties()
        conf.put("user","root")
        conf.put("password", "123456")
        val order_info = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "order_info", conf)

        val win1 = Window.orderBy("id")
        val win2 = Window.orderBy("diff")
//        val order_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_info")
        order_info
            .withColumn("add_amount", sum("final_total_amount").over(win1))
            .withColumn("diff", abs(col("add_amount") - 2023060600))
            .withColumn("row", row_number().over(win2))
//            .where(col("row") <= 10)
            .orderBy("row")
            .limit(10)
            .select("id", "diff")
            .withColumnRenamed("id", "order_id")
            .withColumnRenamed("diff", "diff_value")
            .write
            .mode(SaveMode.Overwrite)
            .jdbc("jdbc:mysql://192.168.45.13:3306/shtd_result?useSSL=false", "order_final_money_amount_diff", conf)
    }
}
