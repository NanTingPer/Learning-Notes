object yg8js04 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        import org.apache.spark.sql.expressions._
        import org.apache.spark.sql.functions._
        import org.apache.spark.sql.{SaveMode, SparkSession}

        import java.util.Properties
        import org.apache.spark.sql.Row
        import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`
        import java.util
        val spark = SparkSession.builder()
            .enableHiveSupport()
            .master("local[*]")
            .appName("qwe")
            .config("hive.exec.dynamic.partition", "true")
            .config("hive.exec.dynamic.partition.mode", "constrict")
            .config("spark.sql.sources.partitionOverwriteMode", "dynamic")
            .config("spark.sql.parquet.writeLegacyFormat", "true")
            .getOrCreate()

        val conf: Properties = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        val url = "jdbc:mysql://192.168.45.13:3306/shtd_result?useSSL=false"
//        val order_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/fact_order_info")
//        val province = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_province").where(col("etl_date") === "20250326")
//        val region = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/dwd_ds_hudi.db/dim_region").where(col("etl_date") === "20250326")
//        val user_info = spark.read.format("hudi").load("hdfs:///user/hive/warehouse/ods_ds_hudi.db/user_info")
        val order_info = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false","order_info", conf)
        val province = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false","base_province", conf)
        order_info.createOrReplaceTempView("order_info")

//        spark.sql(
//            """
//              |SELECT
//              |    (SELECT COUNT(DISTINCT user_id) FROM order_info) AS purchaseduser,
//              |    (SELECT COUNT(DISTINCT a.user_id)
//              |     FROM order_info a
//              |     JOIN order_info b ON a.user_id = b.user_id
//              |     WHERE DATEDIFF(DATE(b.create_time), DATE(a.create_time)) = 1) AS repurchaseduser,
//              |    CONCAT(
//              |        ROUND(
//              |            (SELECT COUNT(DISTINCT a.user_id)
//              |             FROM order_info a
//              |             JOIN order_info b ON a.user_id = b.user_id
//              |             WHERE DATEDIFF(DATE(b.create_time), DATE(a.create_time)) = 1) * 100.0 /
//              |            (SELECT COUNT(DISTINCT user_id) FROM order_info),
//              |        1
//              |        ),
//              |        '%'
//              |    ) AS repurchaserate
//              |""".stripMargin).show

        val sfdd = order_info.groupBy("province_id")
            .agg(count("*") as "Amount")
            .join(province, province("id") === col("province_id"))
            .select("name", "Amount")
            .withColumnRenamed("name", "province_name")
            .orderBy(col("Amount").desc)

        //todo 获取一条数据(不用手动创建新df)
        val r = sfdd.limit(1)

        //todo 将临时df收集并放到drvi端创建
        val rows : Seq[Row] = r.collect().toSeq

        //todo 此列表存放r的列
        val list = new util.ArrayList[Row]()
        rows.foreach(list.add)

        //todo 此列表存放全部计算出来的结果(Row中0是城市名称。1是数量)
        val list2 = new util.ArrayList[Row]()
        sfdd.collect().toSeq.foreach(list2.add)

        //todo 将r恢复为df(drvi端)
        var fff = spark.createDataFrame(list, r.schema)

        //todo 遍历结果(Row中0是城市名称。1是数量)
        list2.foreach(f => {
            fff = fff.withColumn(f.get(0).toString, lit(f.getLong(1)))
        })

        //todo 删除偷懒的列
        fff
            .drop("province_name", "Amount")
            .show()

//        frame
//            .write
//            .mode(SaveMode.Overwrite)
//            .jdbc(url, "userrepurchasedrate", conf)

    }
}
