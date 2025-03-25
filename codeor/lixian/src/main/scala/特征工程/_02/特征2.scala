package 特征工程._02

import org.apache.spark.sql.{DataFrame, Row, SaveMode, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.DataTypes

object 特征2 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        // 创建SparkSession实例
        val spark = SparkSession.builder()
            .master("local[*]")
            .appName("features01")
            .enableHiveSupport()
            .getOrCreate()

        // 执行数据挖掘部分-任务2
        featureTask2(spark)
    }

    // 任务2实现
    def featureTask2(spark:SparkSession): Unit = {
        // 获取相关商品表（sku_info）的id、spu_id、price、weight、tm_id、category3_id 这六个字段
        val sku_info_data = spark.sql(
            """
              |select id,spu_id,price,weight,tm_id,category3_id
              |from dwd.dim_sku_info
              |where etl_date='20250310'
              |""".stripMargin)

        // 对price、weight列进行规范化(StandardScaler)处理
        // 统计指定列的摘要信息（只包含均值mean和标准差stddev）,然后分别提取各个特征值的均值和标准差
        //计算平均值和标准差，mean在summary的介绍中是计算均值
        val sku_info_summary = sku_info_data.select("price","weight").summary("mean","stddev")

        //提取price的均值并拿出数据，后面几段同理
        val mean_price = sku_info_summary
            .where(col("summary")==="mean")
            .select(col("price").cast(DataTypes.DoubleType))
            .first.getDouble(0)

        val stddev_price = sku_info_summary
            .where(col("summary")==="stddev")
            .select(col("price").cast(DataTypes.DoubleType))
            .first
            .getDouble(0)

        val mean_weight = sku_info_summary
            .where(col("summary")==="mean")
            .select(col("weight").cast(DataTypes.DoubleType))
            .first
            .getDouble(0)

        val stddev_weight = sku_info_summary
            .where(col("summary")==="stddev")
            .select(col("weight").cast(DataTypes.DoubleType))
            .first
            .getDouble(0)

        //使用公式计算出  (z-score标准化公式 = (value - mean_value)/stddev_value)
        //value是原始值，mean_value是平均值，stddev_value是标准差
        val sku_info_data_1 = sku_info_data
            .withColumn("price", (col("price") - mean_price)/stddev_price)//price的规范化
            .withColumn("weight", (col("weight") - mean_weight)/stddev_weight)//weight的规范化

        val spuIdSpaces = sku_info_data_1 //全部的spu_id (去重后)
            .select("spu_id")
            .distinct().rdd.map(r => r(0))
            .collect()

        val tmIdSpaces = sku_info_data_1//全部的tm_id (去重后)
            .select("tm_id")
            .distinct().rdd.map(r => r(0))
            .collect()

        val category3IdSpaces = sku_info_data_1//全部的category3_id (去重后)
            .select("category3_id")
            .distinct().rdd.map(r => r(0))
            .collect()

        //One Hit 1
        val sku_info_dummy_1 = spuIdSpaces
            .foldLeft(sku_info_data_1)((sku_info_data_1 : DataFrame, category : Any) =>sku_info_data_1.withColumn("spu_id#"+category.asInstanceOf[Long], when(col("spu_id") === category, 1).otherwise(0)))

        val sku_info_dummy_2 = tmIdSpaces
            .foldLeft(sku_info_dummy_1)((sku_info_data_1, category) => sku_info_data_1.withColumn("tm_id#"+category.asInstanceOf[Long]+"#", when(col("tm_id") === category, 1).otherwise(0)))

        val sku_info_dummy_3 = category3IdSpaces
            .foldLeft(sku_info_dummy_2)((sku_info_data_1, category) => sku_info_data_1.withColumn("category3_id#"+category.asInstanceOf[Long]+"#", when(col("category3_id") === category, 1).otherwise(0)))

        val sku_info_dummy = sku_info_dummy_3.drop("spu_id","tm_id","category3_id").orderBy("id")

        val first_row = sku_info_dummy.first
        for(index <- 0 to 9){
            print(first_row(index) + ",")
        }

        println()

    }
}