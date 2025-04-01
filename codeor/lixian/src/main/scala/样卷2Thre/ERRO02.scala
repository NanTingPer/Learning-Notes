package 样卷2Thre


object ERRO02 {
    def main(args: Array[String]): Unit = {
        import java.util.Properties
        import org.apache.spark.sql.SaveMode
        import org.apache.spark.sql.SparkSession
        import org.apache.spark.sql.functions._
        import org.apache.spark.serializer.KryoSerializer
        import org.apache.spark.sql.hudi.HoodieSparkSessionExtension
        // spark-sql \
        // --conf "spark.serializer=org.apache.spark.serializer.KryoSerializer" \
        // --conf "spark.sql.extensions=org.apache.spark.sql.hudi.HoodieSparkSessionExtension" \
        // --jars

        val spark = SparkSession
            .builder()
            .master("local[*]")
            .enableHiveSupport()
            .appName("hive")
            .getOrCreate()

        //todo 取出订单表并过滤
        val conf1 = new Properties();conf1.put("user","root");conf1.put("password","123456")
//        val order_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_info")
        val order_info = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "order_info", conf1)
            .where(year(col("create_time")) === 2020)
            .select("final_total_amount", "province_id", "id")

        //todo 取出地区表与城市表并Join
//        val region = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_region").where(col("etl_date") === "20250326")
//        val province = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_province").where(col("etl_date") === "20250326")
        val region = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "base_region", conf1)
        val province = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "base_province", conf1)
        val regionJoinProvince = province.join(region, province("region_id") === region("id"))
            .select(province("id") as "provinceid", province("name") as "provincename", region("id") as "regionid", region("region_name") as "regionname")
        val finTable = order_info
            .join(regionJoinProvince, col("province_id") === regionJoinProvince("provinceid"))
//        regionJoinProvince.show

        //todo 使用函数计算中位数
        val zws_sf = finTable
            .groupBy("provinceid", "provincename", "regionid")
            .agg(expr("PERCENTILE(final_total_amount, 0.5)") as "provincemedian")
//            .agg(percentile_approx(col("final_total_amount"), lit(0.5), lit(50000)) as "provincemedian")
            .withColumnRenamed("provinceid", "sfprovinceid")
            .withColumnRenamed("provincename", "sfprovincename")
            .withColumnRenamed("regionid", "sfregionid")
//        zws_sf.show

        val zws_dq = finTable
            .groupBy("regionid", "regionname")
            .agg(expr("PERCENTILE(final_total_amount, 0.5)") as "regionmedian")
//            .agg(percentile_approx(col("final_total_amount"), lit(0.5), lit(50000)) as "regionmedian")
            .withColumnRenamed("regionid","dqregionid")
            .withColumnRenamed("regionname","dqregionname")
//        zws_dq.show

        val fintable = zws_sf.join(zws_dq, zws_sf("sfregionid") === zws_dq("dqregionid"), "left")
            .orderBy("sfprovinceid")
        fintable.show

        val conf = new Properties()
        fintable
            .select(
                col("sfprovinceid"),
                col("sfprovincename"),
                col("dqregionid"),
                col("dqregionname"),
                col("provincemedian"),
                col("regionmedian"))
            .withColumnRenamed("sfprovinceid","provinceid")
            .withColumnRenamed("sfprovincename","provincename")
            .withColumnRenamed("dqregionid","regionid")
            .withColumnRenamed("dqregionname","regionname")

            .write
            .mode(SaveMode.Append)
            .jdbc("jdbc:clickhouse://192.168.45.10:8123/shtd_result","nationmedian", conf)
    }
}

//2、	请根据dwd_ds_hudi层的相关表，计算出2020年每个省份所在地区的订单金额的中位数,存入ClickHouse数据库shtd_result的nationmedian表中（表结构如下），然后在Linux的ClickHouse命令行中根据地区表主键，省份表主键均为升序排序，查询出前5条，将SQL语句复制粘贴至客户端桌面【Release\任务B提交结果.docx】中对应的任务序号下，将执行结果截图粘贴至客户端桌面【Release\任务B提交结果.docx】中对应的任务序号下；
//提示：可用percentile函数求取中位数。
