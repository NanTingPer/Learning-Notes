import org.apache.spark.sql.types.DataTypes

object ERRO03 {
    def main(args: Array[String]): Unit = {
        import org.apache.spark.sql.SparkSession
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.expressions._
        import java.util.Properties
        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .appName("hive")
            .getOrCreate()

        //todo 获取order_info表信息 并过滤
        val order_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_info")
            .where(year(col("create_time")) === 2020)
            .where(month(col("create_time")) === 4)
            .select("final_total_amount", "province_id")

        //todo 计算平均金额
        val win1 = Window.partitionBy()
        val avgsum = order_info
            .groupBy("province_id")
            .agg(count("*") as "shuliang", sum("final_total_amount") as "je")
            .withColumn("provinceavgconsumption", col("je") / col("shuliang"))
            .withColumn("allprovinceavgconsumption", sum("provinceavgconsumption").over(win1) / count("*").over(win1))

        //todo join 地区省份表
        val province = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_province")
            .withColumnRenamed("id", "provinceid")
            .withColumnRenamed("name", "provincename")
        val avgSumJoin = avgsum
            .join(province, avgsum("province_id") === province("provinceid"))
            .select("provinceid","provincename","provinceavgconsumption","allprovinceavgconsumption")

        //todo udf比较
        val ufd1 = udf((d1 : Double, d2 : Double) => if(d1 > d2) "高" else if (d1 < d2) "低" else "相同")
        val fin = avgSumJoin.withColumn("comparison", ufd1(col("provinceavgconsumption"), col("allprovinceavgconsumption")))
        fin.show
        val conf = new Properties();conf.put("user","root");conf.put("password","123456")
        fin
            .withColumn("provinceid",col("provinceid").cast(DataTypes.IntegerType))
            .withColumn("provincename",col("provincename").cast(DataTypes.StringType))
            .withColumn("provinceavgconsumption",col("provinceavgconsumption").cast(DataTypes.DoubleType))
            .withColumn("allprovinceavgconsumption",col("allprovinceavgconsumption").cast(DataTypes.DoubleType))
            .write
            .jdbc("jdbc:mysql://192.168.45.13:3306/shtd_result?useSSL=false","provinceavgcmp",conf)
    }
}
//3、	请根据dwd层表计算出2020年4月每个省份的平均订单金额和所有省份平均订单金额相比较结果（“高/低/相同”）,存入MySQL数据库shtd_result的provinceavgcmp表（表结构如下）中，然后在Linux的MySQL命令行中根据省份表主键、该省平均订单金额均为降序排序，查询出前5条，将SQL语句复制粘贴至客户端桌面【Release\任务B提交结果.docx】中对应的任务序号下，将执行结果截图粘贴至客户端桌面【Release\任务B提交结果.docx】中对应的任务序号下;