package 样卷2Thre.计算错误03

import org.apache.spark.sql.expressions._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{SaveMode, SparkSession}

import java.util.Properties

object erro02 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession
            .builder()
            .master("local[*]")
            .appName("dawf")
            .enableHiveSupport()
            .getOrCreate()

        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        val order_info = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "order_info", conf).where(year(col("create_time")) === 2020).where(month(col("create_time")) === 4)
        val base_province = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "base_province", conf)
        val base_region = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "base_region", conf)

        //todo 省份中位数
        val provinceZWS = order_info
            .groupBy("province_id")
            .agg(expr("percentile(final_total_amount, 0.5)") as "provincemedian")

        //todo 地区中位数
        val regionZWS = order_info
            .join(base_province, col("province_id") === base_province("id"))
            .groupBy("region_id")
            .agg(expr("percentile(final_total_amount, 0.5)") as "regionmedian")

        provinceZWS
            .join(base_province, col("province_id") === base_province("id"))
            .join(base_region, base_region("id") === col("region_id"))
            .join(regionZWS, "region_id")
            .select(
                "province_id",
                "name",
                "region_id",
                "region_name",
                "provincemedian",
                "regionmedian")
            .show
    }
}

//1、
//      请根据dwd层表计算出2020年4月每个省份的平均订单金额和所有省份平均订单金额相比较结果（“高/低/相同”）,
//      存入MySQL数据库shtd_result的provinceavgcmp表（表结构如下）中，
//      然后在Linux的MySQL命令行中根据省份表主键、该省平均订单金额均为降序排序，
//      查询出前5条，
//      将SQL语句复制粘贴至客户端桌面【Release\任务B提交结果.docx】中对应的任务序号下，
//      将执行结果截图粘贴至客户端桌面【Release\任务B提交结果.docx】中对应的任务序号下;
