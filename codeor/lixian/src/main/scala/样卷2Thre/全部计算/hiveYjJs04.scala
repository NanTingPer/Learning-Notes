package 样卷2Thre.全部计算

import org.apache.spark.sql.expressions._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{SaveMode, SparkSession}

import java.util.Properties
import scala.tools.cmd.Property


object hiveYjJs04 {
  def main(args: Array[String]): Unit = {
    System.setProperty("HADOOP_USER_NAME","root")
    val spark = SparkSession.builder()
      .master("local[*]")
      .appName("qwe")
      .config("hive.exec.dynamic.partition", "true")
      .config("hive.exec.dynamic.partition.mode", "constrict")
      .config("spark.sql.sources.partitionOverwriteMode", "dynamic")
      .config("spark.sql.parquet.writeLegacyFormat", "true")
      .enableHiveSupport()
      .getOrCreate()

    val w1 = Window.partitionBy("region_id").orderBy(col("provinceamount").desc)
    val province_consumption_day_aggr = spark.sql("select * from dws.province_consumption_day_aggr")
      .groupBy("region_id", "region_name", "province_id", "province_name")
      .agg(sum("total_amount") as "provinceamount")
      .withColumn("pm", row_number().over(w1))
      .where("pm<=3")
      .withColumn("provinceamount", col("provinceamount").cast("int"))
    val win3 = Window.partitionBy("region_id").orderBy("province_id")
    val conf = new Properties()
    conf.put("user", "root")
    conf.put("password", "123456")

    province_consumption_day_aggr
      .withColumn("down1province_id", lead("province_id", 1).over(win3))
      .withColumn("down2province_id", lead("province_id", 2).over(win3))
      .withColumn("down1province_name", lead("province_name", 1).over(win3))
      .withColumn("down2province_name", lead("province_name", 2).over(win3))
      .withColumn("down1provinceamount", lead("provinceamount", 1).over(win3))
      .withColumn("down2provinceamount", lead("provinceamount", 2).over(win3))
      .withColumn("province_id", concat(col("province_id"), lit(","), col("down1province_id"), lit(","), col("down2province_id")))
      .withColumn("province_name", concat(col("province_name"), lit(","), col("down1province_name"), lit(","), col("down2province_name")))
      .withColumn("provinceamount", concat(col("provinceamount"), lit(","), col("down1provinceamount"), lit(","), col("down2provinceamount")))
      .select("region_id", "region_name", "province_id", "province_name", "provinceamount")
      .where(col("province_id") =!= "null" and col("province_name") =!= "null" and col("provinceamount") =!= "null")
      .write.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_result?useSSL=false", "shtd_result.regiontopthree", conf)

  }
}
