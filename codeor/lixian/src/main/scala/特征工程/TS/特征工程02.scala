package 特征工程.TS

import org.apache.spark.sql.types.DataTypes

import java.util


object 特征工程02 {
    def main(args: Array[String]): Unit = {

        import org.apache.spark.sql.SparkSession
        import org.apache.spark.sql.api.java.UDF2
        import org.apache.spark.sql.functions._
        import java.util.Properties
//        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession.builder()
            .master("local[*]")
            .appName("qwe")
            .enableHiveSupport()
            .getOrCreate()
        val conf = new Properties()
        conf.put("user","root")
        conf.put("password","123456")
        val sku_info = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false","sku_info",conf)
                .select("id","spu_id","price","weight","tm_id","category3_id")
        val cl_sku = sku_info.select("price","weight").summary("stddev","mean")

        val price_stddev = cl_sku.where(col("summary")==="stddev").select("price").first().get(0)
        val price_mean = cl_sku.where(col("summary")==="mean").select("price").first().get(0)
        val weight_stddev = cl_sku.where(col("summary")==="stddev").select("weight").first().get(0)
        val weight_mean = cl_sku.where(col("summary")==="mean").select("weight").first().get(0)
        var tab1 = sku_info
            .withColumn("price",(col("price")-price_mean)/price_stddev)
            .withColumn("weight",(col("weight")-weight_mean)/weight_stddev)

        val spu_row = sku_info.select("spu_id").distinct().orderBy("spu_id").collect().map(f=>f.get(0))
        val tm_row= sku_info.select("tm_id").distinct().orderBy("tm_id").collect().map(f=>f.get(0))
        val category3_row= sku_info.select("category3_id").distinct().orderBy("category3_id").collect().map(f=>f.get(0))
        val broad_spu = spark.sparkContext.broadcast(spu_row)
        val broad_tm = spark.sparkContext.broadcast(tm_row)
        val broad_category3 = spark.sparkContext.broadcast(category3_row)


        val udf1 = udf((t1:Long,t2:Long) => if(t1==t2) "1.0" else "0.0")

        broad_spu.value.foreach(f=>
            tab1 = tab1.withColumn(s"spu_id#${f}",udf1(lit(f),col("spu_id")))
        )
        broad_tm.value.foreach(f=>
            tab1 = tab1.withColumn(s"tm_id#${f}",udf1(lit(f),col("tm_id")))
        )
        broad_category3.value.foreach(f=>
            tab1 = tab1.withColumn(s"category3_id#${f}",udf1(lit(f),col("category3_id")))
        )


        val end_tab = tab1
            .drop("spu_id","tm_id","category3_id")
            .withColumn("id",col("id").cast(DataTypes.DoubleType))
            .limit(1)
            .first()

        println()
        println("--------------------第一条数据前10列结果展示为：---------------------2")

        for (i <- 0 to 9)
            if(i != 9) print(end_tab.get(i)+",") else print(end_tab.get(i))
        println()
        println()
    }
}
