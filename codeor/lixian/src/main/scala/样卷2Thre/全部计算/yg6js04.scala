package 样卷2Thre.全部计算

import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.types.DataTypes



object yg6js04 {
    def main(args: Array[String]): Unit = {
        import org.apache.spark.sql.SparkSession
        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.functions._

        import java.util.Properties
        val spark = SparkSession.builder().enableHiveSupport().appName("wfawfawf")
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .config("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
            .getOrCreate()

        val order_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_info")
        val province = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_province").where(col("etl_date") === "20250326")
        val region = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_region").where(col("etl_date") === "20250326")

        val win1 = Window.partitionBy("provinceid")
        val win2 = Window.partitionBy("provinceid").orderBy("final_total_amount")
        val Oneorder_info = order_info.where(year(col("create_time")) === "2020")
            .select("province_id", "final_total_amount")
            .join(province, col("province_id") === province("id"))
            .select("province_id", "final_total_amount", "name", "region_id")
            .withColumnRenamed("name", "provincename")
            .withColumnRenamed("province_id", "provinceid")
            .withColumnRenamed("region_id", "regionid")
            .join(region, col("regionid") === region("id"))
            .select("provinceid", "provincename", "regionid", "region_name", "final_total_amount")
            .withColumnRenamed("region_name", "regionname")

        //省份中位数
        val sfzws = Oneorder_info
            .groupBy("provinceid", "provincename","regionid", "regionname")
            .agg(percentile_approx(col("final_total_amount"), lit(0.5), lit(10000)) as "provincemedian")

        val dqzws = Oneorder_info
            .groupBy("regionid", "regionname")
            .agg(percentile_approx(col("final_total_amount"), lit(0.5), lit(10000)) as "regionmedian")
        val conf = new Properties()
        conf.put("user","default")
        sfzws.join(dqzws, Seq("regionid","regionname"), "left")
            .select("provinceid","provincename","regionid","regionname","provincemedian","regionmedian")
            .write
            .mode(SaveMode.Append)
            .jdbc("jdbc:clickhouse://192.168.45.10:8123/shtd_result", "nationmedian", conf)

        //            .withColumn("sfddje", sum("final_total_amount").over(win1))
//            .withColumn("TEMP", row_number().over(win2))
//            .where(col("TEMP") === 1)
//            .drop("TEMP")
//            .withColumn("provincemedian", percentile_approx())

//        val win3 = Window.orderBy("sfddje")
//        val r =
//            sf.select("sfddje")
//            .orderBy("sfddje")
//            .withColumn("num", row_number().over(win3))
//
//        //省中位数
//        val countd = r.select(count("*")).first()
//        var zws : Double = 0d
//        if(countd % 2 == 0) {
//            var temp = 0d
//            for (elem <- r.where(col("num") === countd / 2 or col("num") === (countd / 2) + 1)
//                .select("sfddje")
//                .collect().map(f => f.getDouble(0))) {
//                temp += elem
//            }
//            zws = temp/2f
//        }
//        else zws = r.where(col("num") === countd / 2).select("sfddje").first().getDouble(0)
//        sf.withColumn("provincemedian", lit(s"${zws}"))
//
//        val win4 = Window.partitionBy("regionid")
//        val win5 = Window.partitionBy("regionid").orderBy("sfddje")
//
//        //地区
//        sf
//            .withColumn("dqje", sum("sfddje").over(win5))
//            .show

//            .write
//            .mode(SaveMode.Append)
//            .jdbc("jdbc:clickhouse://192.168.45.10:8123/shtd_result", "topten", conf)


    }
}
