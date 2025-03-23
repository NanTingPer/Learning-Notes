package 特征工程._02

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.api.java.{UDF1, UDF2}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.DataTypes

import java.util
import java.util.Properties

object 样3特征102 {
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

        val user_idMap = userSkuId
            .select(col("user_id"))
            .distinct()
            .orderBy("user_id")
            .collect()
            .map(f => f.get(0).asInstanceOf[Long])
            .zipWithIndex
            .toMap
        val sku_idMap = userSkuId
            .select(col("sku_id"))
            .distinct()
            .orderBy("sku_id")
            .collect()
            .map(f => f.get(0).asInstanceOf[Long])
            .zipWithIndex
            .toMap

        val user_idUDF = udf((f : Long) => user_idMap.getOrElse(f, -1))
        val sku_idUDF = udf((f : Long) => sku_idMap.getOrElse(f, -1))

        val tz1Table = userSkuId
            .withColumn("user_id", user_idUDF(col("user_id")))
            .withColumn("sku_id", sku_idUDF(col("sku_id")))
            .orderBy("user_id")



        val map = new util.HashMap[Int, util.HashSet[Int]]()
//        val map = Map[Int, Set[Int]]
        //TODO 特征2
        tz1Table.collect().map(f => (f.get(0).asInstanceOf[Int], f.get(1).asInstanceOf[Int])).foreach(f => {
            if(map.containsKey(f._1)){
                val longs = map.get(f._1)
                longs.add(f._2)
            }else{
                val list = new util.HashSet[Int]();
                list.add(f._2)
                map.put(f._1, list)
            }
        })


        val gdmap = spark.sparkContext.broadcast(map)

        val udf1 = udf((user_id:Integer, sku_id:Integer) => {
            Option(gdmap.value.get(user_id)).exists(f => f.contains(sku_id))
//            val r = gdmap.value
//            if(r.containsKey(user_id)){
//                r.get(user_id).contains(sku_id)
//            }else{
//                r.containsKey(user_id)
//            }
        })

        spark.udf.register("boolToInt", new UDF1[Boolean, Int] {override def call(t1: Boolean): Int = if(t1) 1 else  0}, DataTypes.IntegerType)

        val sku_idTable = userSkuId.select("sku_id").distinct().collect().map(f => f.get(0).asInstanceOf[Long])
        var table = userSkuId.select("user_id").distinct()

        sku_idTable.foreach(f => {
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
