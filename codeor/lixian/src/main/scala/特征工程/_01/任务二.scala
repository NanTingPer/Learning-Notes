package 特征工程._01

import org.apache.spark.sql.functions._

object 任务二 {

    //根据Hive 的dwd 库中相关表或MySQL中shtd_store中相关商品表（sku_info），
    // 获取id、spu_id、price、weight、tm_id、category3_id 这六个字段并进行数据预处理，
    // 对price、weight 进行规范化(StandardScaler)处理
    // ，对spu_id、tm_id、category3_id 进行one-hot 编码处理
    // （若该商品属于该品牌则置为1，否则置为0）,
    // 并按照id 进行升序排序，
    // 在集群中输出第一条数据前10 列（无需展示字段名）
    //1.0,0.892346,1.72568,0.0,0.0,0.0,0.0,1.0,0.0,0.0
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = Test04.Util.GetSpark
        val mysql_table = Test04.Util.GetMySQLData(spark, "shtd_store", "sku_info")
        val tab1 = mysql_table.select("id","spu_id","price","weight","tm_id","category3_id")
        val tab2 = mysql_table.select("price","weight").summary("mean","stddev")
        val price_mean = tab2.where(col("summary")==="mean").first()(1)
        val price_stddev = tab2.where(col("summary")==="stddev").first()(1)
        val weight_mean = tab2.where(col("summary")==="mean").first()(2)
        val weight_stddev = tab2.where(col("summary") === "stddev").first()(2)
        val sku_info_data_1 = tab1
            .withColumn("price_2",(col("price")-price_mean)/price_stddev)
            .withColumn("weight_2",(col("weight")-weight_mean)/weight_stddev)

        //对spu_id、tm_id、category3_id 进行one-hot 编码处理
        // （若该商品属于该品牌则置为1，否则置为0）,
        // 并按照id 进行升序排序，
        // 在集群中输出第一条数据前10 列（无需展示字段名）
//        val spu_id = mysql_table.select("spu_id").distinct().rdd.collect()
//        val tm_id = mysql_table.select("tm_id").distinct().rdd.collect()
//        val category3_id = mysql_table.select("category3_id").distinct().rdd.collect()
        val spuIdSpaces = sku_info_data_1.select("spu_id").distinct().rdd.map(r => r(0)).collect()
        val tmIdSpaces = sku_info_data_1.select("tm_id").distinct().rdd.map(r => r(0)).collect()
        val category3IdSpaces = sku_info_data_1.select("category3_id").distinct().rdd.map(r => r(0)).collect()

        val sku_info_dummy_1 = spuIdSpaces
            .foldLeft(sku_info_data_1)((sku_info_data_1, category) => sku_info_data_1.withColumn("spu_id#"+category.asInstanceOf[Long], when(col("spu_id") === category, 1).otherwise(0)))
        val sku_info_dummy_2 = tmIdSpaces
            .foldLeft(sku_info_dummy_1)((sku_info_data_1, category) => sku_info_data_1.withColumn("tm_id#"+category.asInstanceOf[Long]+"#", when(col("tm_id") === category, 1).otherwise(0)))
        val sku_info_dummy_3 = category3IdSpaces
            .foldLeft(sku_info_dummy_2)((sku_info_data_1, category) => sku_info_data_1.withColumn("category3_id#"+category.asInstanceOf[Long]+"#", when(col("category3_id") === category, 1).otherwise(0)))

        val sku_info_dummy = sku_info_dummy_3.drop("spu_id","tm_id","category3_id").orderBy("id")
        sku_info_dummy.show()

        // 第一条数据前10列
        val first_row = sku_info_dummy.first
        for(index <- 0 to 9){
            print(first_row(index) + ",")
        }
        println()









//        连续特征的标准化处理
//            .withColumn("spu_id1", lit("1"))
//            .withColumn("tm_id1", lit("1"))
//            .withColumn("category3_id1", lit("1"))
//            .rdd
//            .map(spu => spu.)

//        {
//            var bolen = false;
//            spu_id.foreach(f => {
//                if(spu(1) == f)
//                    bolen = true;
//            })
//            if(bolen)
//                spu.se
//        }


    }
}/*
说明：
在Spark ML机器学习库中，本来提供的是有org.apache.spark.ml.feature.StandardScaler类用来执行特征的z-score标准化处理，
但题目中要求的结果展现形式比较奇葩，直接用StandardScaler虽然计算简单，但转换成它要的形式比较麻烦。
因此，我们这里直接使用z-score标准化公式来进行特征的标准化转换。
z-score标准化公式 = (value - mean_value)/stddev_value
其中：value为特征值，mean_value为该特征值的均值，stddev_value为该特征值的标准差。
*/