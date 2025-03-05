package Test04

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.api.java.{UDF2, UDF3}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.DataTypes

import java.util.Properties

object 计算01 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME", "root")
        val spark = SparkSession
            .builder()
            .master("local[*]")
            .enableHiveSupport()
            .appName("hive")
            .getOrCreate()
        ///根据dwd层表统计每个省份、每个地区、每个月下单的数量和下单的总金额，
        /// 存入MySQL数据库 shtd_result 的provinceeverymonth表中（表结构如下），
        /// 然后在Linux的MySQL命令行中根据订单总数、订单总金额、省份表主键均为降序排序，
        // 查询出前5条，将SQL语句复制粘贴至客户端桌面【Release\任务B提交结 果.docx】中对应的任务序号下，
        // 将执行结果截图粘贴至客户端桌
        val province = spark.sql("select *, id as province_idTemp from dwd.dim_province")

        val region = spark.sql("select * from dwd.dim_region") //地区
        val info = spark.sql("select * from dwd.fact_order_info") //Info
        val detail = spark.sql("select * from dwd.fact_order_detail") //Detail

        val infoAndDetail = info.join(detail, info.col("id") === detail.col("order_id"))
            .select("order_id", "province_id", "order_price", "final_total_amount")

        val AndProvinceJoin = infoAndDetail.join(province, infoAndDetail.col("province_id") === province.col("id"))
            .select("province_id", "order_id", "name", "region_id", "order_price", "final_total_amount")

        val AndRegionJoin = AndProvinceJoin.join(region, region.col("id") === AndProvinceJoin.col("region_id"))
            .select("province_id", "name", "region_id", "region_name", "order_id", "order_price", "final_total_amount")

        val win1 = Window.partitionBy("province_id")
        val win2 = Window.partitionBy("province_id").orderBy("province_id")
        /*val 省平均订单金额 = */AndRegionJoin
            .withColumn("temp", sum("order_price").over(win1))
            .withColumn("count", count("*").over(win1))
            .withColumn("num", row_number().over(win2))
            .where(col("num") === 1)
            .withColumn("provinceavgconsumption", (col("temp") / col("count")).cast(DataTypes.DoubleType))
            .drop("temp", "count", "num", "order_price", "final_total_amount").createTempView("spjdd")

        spark.udf.register("avgpro", new UDF2[Double, Double, String] {
            override def call(t1: Double, t2: Double): String = {
                val t1double = t1.toDouble
                val t2double = t2.toDouble
                if(t1double > t2double) "高"
                else if(t1double < t2double) "低"
                else "相同"
            }
        }, DataTypes.StringType)

        val 各省全部平均 = spark.sql("select sum(provinceavgconsumption) from spjdd").first()(0)
        val 省份数量 = spark.sql("select count(*) from spjdd").first()(0)
        val 全省平均 = 各省全部平均.toString.toDouble / 省份数量.toString.toDouble
        spark.sql("select * from spjdd").withColumn("allprovinceavgconsumption", lit(全省平均).cast(DataTypes.DoubleType)).createTempView("find")
        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        spark.sql("select *, avgpro(provinceavgconsumption,allprovinceavgconsumption) from find")
            .write.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_result?useSSL=false", "provinceavgcmp", conf)


    }
}
