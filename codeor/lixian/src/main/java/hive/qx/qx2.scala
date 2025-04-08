package hive.qx

object qx2 {
    def main(args: Array[String]): Unit = {

        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.types.DataTypes
        import org.apache.spark.sql.{SaveMode, SparkSession}
        val spark = SparkSession
            .builder()
            .config("hive.exec.dynamic.partition.mode","nonstrict")
            .master("local[*]")
            .appName("hive")
            .enableHiveSupport()
            .getOrCreate()

        //ods
        val ods = spark.table("ods.sku_info").where(col("etl_date") === "20250407")

        //dim_user_info
        val dwd = spark.table("dwd.dim_sku_info")
        val dwdmaxpartition = dwd.select(max("etl_date")).first()(0)
        val yxdwd = dwd.where(col("etl_date") === dwdmaxpartition)
        val cols = dwd.columns.map(col)

        //ods in
        val yxods = ods
            .withColumn("dwd_insert_user", lit("user1"))
            .withColumn("dwd_insert_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
            .withColumn("dwd_modify_user", lit("user1"))
            .withColumn("dwd_modify_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss").cast(DataTypes.TimestampType))
            .select(cols: _*)

        val untable = yxods.union(yxdwd)
        val win1 = Window.partitionBy("id").orderBy(col("create_time").desc)
        val win2 = Window.partitionBy("id")

        untable
            .withColumn("temp", row_number().over(win1))
            .withColumn("dwd_insert_time", min("dwd_insert_time").over(win2))
            .withColumn("dwd_modify_time", max("dwd_modify_time").over(win2))
            .where(col("temp") === 1)
            .drop("temp")
            .withColumn("etl_date", lit("20250407"))
            .write
            .mode(SaveMode.Append)
            .format("hive")
            .partitionBy("etl_date")
            .saveAsTable("dwd.dim_sku_info")

    }
}
