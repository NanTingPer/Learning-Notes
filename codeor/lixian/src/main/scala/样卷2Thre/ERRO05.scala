package 样卷2Thre




object ERRO05 {
    def main(args: Array[String]): Unit = {
        import org.apache.spark.sql.SparkSession
        import org.apache.spark.sql.types.DataTypes
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.expressions._
        import java.util.Properties
        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .appName("hive")
            .getOrCreate()
        val table = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dws_ds_hudi.db/province_consumption_day_aggr")
            .where(col("year") === 2020)
        //todo 每个地区订单金额前三的省份
        val win1 = Window.partitionBy("region_id").orderBy(col("provinceamount").desc)
        val ddcsan = table.groupBy("province_id", "province_name", "region_id", "region_name")
            .agg(sum("total_amount") as "provinceamount")
            //订单前三
            .withColumn("pm", row_number().over(win1))
            .where(col("pm") <= 3)

        //todo 合并
        val hb =ddcsan
            .withColumn("downProvinceOne", lead(col("province_name"), 1).over(win1))
            .withColumn("downProvinceTwo", lead(col("province_name"), 2).over(win1))
            .withColumn("downProvinceIdOne", lead(col("province_id"), 1).over(win1))
            .withColumn("downProvinceIdTwo", lead(col("province_id"), 2).over(win1))
            .withColumn("downProvinceamountOne", lead(col("provinceamount"), 1).over(win1))
            .withColumn("downProvinceamountTwo", lead(col("provinceamount"), 2).over(win1))
            .withColumn("num", row_number().over(win1))
            .where(col("num") === 1)
            .withColumn("provinceids", concat(col("province_id"), lit(","), col("downProvinceIdOne"), lit(","), col("downProvinceIdTwo")))
            .withColumn("provincenames", concat(col("province_name"), lit(","), col("downProvinceOne"), lit(","), col("downProvinceTwo")))
            .withColumn("provinceamount", concat(round(col("provinceamount"), 0).cast(DataTypes.IntegerType), lit(","), round(col("downProvinceamountOne"),0).cast(DataTypes.IntegerType), lit(","), round(col("downProvinceamountTwo"),0).cast(DataTypes.IntegerType)))
            .select("region_id", "region_name", "provinceids","provincenames","provinceamount")
            .withColumnRenamed("region_id","regionid")
            .withColumnRenamed("region_name","regionname")
        import org.apache.spark.sql.SaveMode
        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        hb
            .write
            .mode(SaveMode.Overwrite)
            .jdbc("jdbc:mysql://192.168.45.13:3306/shtd_result?useSSL=false", "regiontopthree", conf)

    }
}
