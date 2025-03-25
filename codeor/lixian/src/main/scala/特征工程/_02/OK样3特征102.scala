package 特征工程._02

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.api.java.{UDF1, UDF2}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.DataTypes

import java.util
import java.util.Properties

object OK样3特征102 {
    def main(args: Array[String]): Unit = {
        val spark = SparkSession.builder().master("local[*]").appName("hiveee").enableHiveSupport().getOrCreate()
        val conf = new Properties()
        conf.put("user","root")
        conf.put("password", "123456")
        val order_detail = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "order_detail", conf)
        val order_info = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "order_info", conf)
        val sku_info = spark.read.jdbc("jdbc:mysql://192.168.45.13:3306/shtd_store?useSSL=false", "sku_info", conf)

        val userSkuId = order_info.join(order_detail, order_detail("order_id") === order_info("id"))
            .select("user_id", "sku_id")
            .distinct()

        //获取user_id映射表 (user_id, key)
        val user_idMap = userSkuId
            .select(col("user_id"))
            .distinct()
            .orderBy("user_id")
            .collect()
            .map(f => f.get(0).asInstanceOf[Long])
            .zipWithIndex
            .toMap

        //获取sku_id映射表(sku_id, key)
        val sku_idMap = userSkuId
            .select(col("sku_id"))
            .distinct()
            .orderBy("sku_id")
            .collect()
            .map(f => f.get(0).asInstanceOf[Long])
            .zipWithIndex
            .toMap

        //使用user_id从user_idMap中获取key,没有就是-1
        val user_idUDF = udf((f : Long) => user_idMap.getOrElse(f, -1))
        //使用sku_id从sku_idMap中获取key,没有就是-1
        val sku_idUDF = udf((f : Long) => sku_idMap.getOrElse(f, -1))

        //得到Mapping表，这是第一题的结果
        val tz1Table = userSkuId
            .withColumn("user_id", user_idUDF(col("user_id")))
            .withColumn("sku_id", sku_idUDF(col("sku_id")))
            .orderBy("user_id")



        //TODO 特征2
        //创建字,其中key是用户id,值是用户购买过的商品
        val map = new util.HashMap[Int, util.HashSet[Int]]()
        //遍历上一题的表,第一列是user_id的映射,第二列是sku_id的映射,将sku_id全部放到Set集合
        tz1Table.collect().map(f => (f.get(0).asInstanceOf[Int], f.get(1).asInstanceOf[Int]))
            .foreach(f => {
                if(map.containsKey(f._1)){
                    val longs = map.get(f._1)
                    longs.add(f._2)
                }else{
                    val list = new util.HashSet[Int]();
                    list.add(f._2)
                    map.put(f._1, list)
            }
        })

        //广播Map
        val gdmap = spark.sparkContext.broadcast(map)
        //定义udf用于判断此用户是否购买过此商品
        val udf1 = udf((user_id:Integer, sku_id:Integer) => {
            Option(gdmap.value.get(user_id)).exists(f => f.contains(sku_id))
        })
        //无意义udf用来判断将Bool转换为int
        spark.udf.register("boolToInt", new UDF1[Boolean, Int] {override def call(t1: Boolean): Int = if(t1) 1 else  0}, DataTypes.IntegerType)

        //全部的sku_id,去重了
        val sku_idTable = userSkuId.select("sku_id").distinct().collect().map(f => f.get(0).asInstanceOf[Long])
        var table = userSkuId.select("user_id").distinct()

        //遍历sku_id,因为要把sku_id进行One-hit
        sku_idTable.foreach(f => {
            //带有冗余操作
            table.withColumn(s"sku_id_${f}", udf1(col("user_id").cast(DataTypes.IntegerType), lit(f).cast(DataTypes.IntegerType)).cast(DataTypes.BooleanType))
                .createOrReplaceTempView("temp")
            table = spark.sql(s"select *,boolToInt(sku_id_${f}) as boolrrr from temp")
                .drop(s"sku_id_${f}")
                .withColumnRenamed("boolrrr", s"sku_id#${f}")
        })
        table.orderBy(col("user_id"))
            .show
    }
}
