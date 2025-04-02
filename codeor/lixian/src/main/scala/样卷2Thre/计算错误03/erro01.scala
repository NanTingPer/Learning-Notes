package 样卷2Thre.计算错误03

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions._

import java.util.Properties

object erro01 {
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

        //todo 每个省份平均订单金额
        val sfpjdd = order_info
            .groupBy("province_id")
            .agg((sum("final_total_amount") / count("*")) as "provinceavgconsumption")

        //todo 所有省份平均订单
        val win1 = Window.partitionBy()
        val allprovinceavgconsumption = order_info
            .withColumn("allprovinceavgconsumption", sum("final_total_amount").over(win1) / count("*").over(win1))

        val all = allprovinceavgconsumption.select("allprovinceavgconsumption").limit(1).first()(0)

        val udf1 = udf((d1 : Double, d2 : Double) => if(d1 > d2) "高" else if(d1 < d2) "低" else "相同")
        val provinceAvg = sfpjdd
            .withColumn("allprovinceavgconsumption", lit(all))
            .join(base_province, col("province_id") === base_province("id"))
            .select("province_id", "name", "provinceavgconsumption", "allprovinceavgconsumption")
            .withColumn("comparison",udf1(col("provinceavgconsumption"), col("allprovinceavgconsumption")))
            .withColumnRenamed("province_id", "provinceid")
            .withColumnRenamed("name", "provincename")
            .write
            .mode(SaveMode.Overwrite)
            .jdbc("jdbc:mysql://192.168.45.13:3306/shtd_result?useSSL=false","provinceavgcmp",conf)

    }
}

//1、
//      请根据dwd层表计算出2020年4月每个省份的平均订单金额和所有省份平均订单金额相比较结果（“高/低/相同”）,
//      存入MySQL数据库shtd_result的provinceavgcmp表（表结构如下）中，
//      然后在Linux的MySQL命令行中根据省份表主键、该省平均订单金额均为降序排序，
//      查询出前5条，
//      将SQL语句复制粘贴至客户端桌面【Release\任务B提交结果.docx】中对应的任务序号下，
//      将执行结果截图粘贴至客户端桌面【Release\任务B提交结果.docx】中对应的任务序号下;
