package 样卷2Thre

import java.util.Properties

object ERRO01 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        import org.apache.spark.sql.SparkSession
        import org.apache.spark.sql.types.DataTypes
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.SaveMode

        val spark = SparkSession
            .builder()
            .master("local[*]")
            .appName("h5465")
            .enableHiveSupport()
            .getOrCreate()

        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        //todo 获取表数据
//        val table = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_detail")
        val table = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false","order_detail",conf)
        
        //todo 主键        商品id   商品名称    购买数量    商品价格
        val data = table.select("id", "sku_id", "sku_name", "sku_num", "order_price", "create_time")
            .where(year(col("create_time")) === 2020)

        //todo 销售量前10
        val win1 = Window.orderBy(col("topquantity").desc)
        val xsltop10 = data
            .groupBy("sku_id", "sku_name")
            .agg(sum("sku_num") as "topquantity")
            .withColumn("sequence", row_number().over(win1))
            .withColumnRenamed("sku_id", "topquantityid")
            .withColumnRenamed("sku_name", "topquantityname")


        //todo 销售额前10
        val win2 = Window.orderBy(col("topprice").desc)
        val xshtop10 = data
            .withColumn("pprice", col("sku_num") * col("order_price"))
            .groupBy("sku_id", "sku_name")
            .agg(sum("pprice") as "topprice")
//            .withColumn("topprice", col("topprice").cast(DataTypes.createDecimalType(32, 8)))
            .withColumn("sequence", row_number().over(win2))
            .withColumnRenamed("sku_id","toppriceid")

            .withColumnRenamed("sku_name","toppricename")
            .orderBy("sequence")
        xshtop10.show()
//
//
        val fin = xsltop10.join(xshtop10, xshtop10("sequence") === xsltop10("sequence"))
            .select(
                col("topquantityid"),
                col("topquantityname"),
                col("topquantity"),
                col("toppriceid"),
                col("toppricename"),
                col("topprice").cast(DataTypes.createDecimalType(20, 8)),
                xsltop10("sequence"))

        val conf1 = new Properties()
        fin
            .write
            .mode(SaveMode.Append)
            .jdbc("jdbc:clickhouse://192.168.45.10:8123/shtd_result","topten", conf1)

    }
}

//1、	请根据dwd_ds_hudi层的相关表，计算2020年销售量前10的商品，销售额前10的商品，存入ClickHouse数据库shtd_result的topten表中（表结构如下），然后在Linux的ClickHouse命令行中根据排名升序排序，查询出前5条，将SQL语句复制粘贴至客户端桌面【Release\任务B提交结果.docx】中对应的任务序号下，将执行结果截图粘贴至客户端桌面【Release\任务B提交结果.docx】中对应的任务序号下;
