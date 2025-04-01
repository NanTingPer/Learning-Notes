package 特征工程


object 规范化与OneHit {
    def main(args: Array[String]): Unit = {
        import org.apache.spark.sql.SparkSession
        import org.apache.spark.sql.functions._

        import java.util.Properties
        val spark = SparkSession
        .builder()
            .appName("awf")
            .master("local[*]")
            .enableHiveSupport()
            .getOrCreate()

        val conf = new Properties()
        conf.put("user","root")
        conf.put("password", "123456")
        val sku_info = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "sku_info", conf)

        //TODO 计算平均值与标准差
        val stmean = sku_info.select("price", "weight").summary()
        val stddev = stmean.where(col("summary") === "stddev")
        val mean = stmean.where(col("summary") === "mean")

        //TODO first取出的是一个Row
        val priceMean = mean.first()(1)
        val weightStddev = stddev.first()(2)

        val priceStddev = stddev.first()(1)
        val weightMean = mean.first()(2)


        //TODO z-score标准化处理。计算公式：stddev = (value - mean_value)/stddev_value
        //TODO 每一个price 都按照z-zcore进行计算
        //TODO 每一个weight 都按照z-zcore进行计算

        //TODO 计算price && weight
        var score = sku_info
            .select("price", "weight", "id", "spu_id", "tm_id")
            .withColumn("price", (col("price") - priceMean) / priceStddev) //TODO price规范化
            .withColumn("weight", (col("weight") - weightMean) / weightStddev) //TODO price规范化
            .select("id", "price", "weight", "tm_id")

        //TODO 取出全部tm_id(品牌)
        val tmids = sku_info.select("tm_id").distinct().collect().map(f => f.getLong(0))

        //TODO 遍历tm_id,进行OneHit
        val my_udf = udf((d1 : Long, d2 : Long) => if(d1 == d2) "1.0" else "0.0")
        tmids.foreach(f => score = score.withColumn(s"brand_id#${f}", my_udf(col("tm_id"), lit(f))))
        score.drop("tm_id").show
    }
}
