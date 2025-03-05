package 哈基咪;

object CalculationJob01{

def main(args:Array[String]):Unit ={

// 创建SparkSession实例
val spark = SparkSession.builder()
    .master("local[*]")
    .appName("Data Calculation")
    // 兼容Hive Parquet存储格式
    .config("spark.sql.parquet.writeLegacyFormat", "true")
    .enableHiveSupport()
    .getOrCreate()

// 加载dwd的fact_order_master表，并过滤出2022年已下单和已付款的订单
    spark.

table("dwd.fact_order_master").

createOrReplaceTempView("fact_order_master_tb")

val sql1 =
    """
        |select
        |    order_id,order_sn,order_status,province,create_time
        |from
        |    fact_order_master_tb
        |where
        |    (order_status='已下单' or order_status='已付款') and substr(create_time,0,4)='2022'
        |""".stripMargin

// 将过滤出的数据注册到临时视图
    spark.

sql(sql1).

createOrReplaceTempView("order_2022_tb")

// 分别统计各省已下单数和各省已付款单数，然后join连接
val sql2 =
    """
        |with tb1 as(
        |  select province, count(1) as creat_order
        |  from   order_2022_tb
        |  where  order_status='已下单'
        |  group by province
        |), tb2 as(
        |  select province, count(1) as payment
        |  from   order_2022_tb
        |  where  order_status='已付款'
        |  group by province
        |)
        |select tb1.province, creat_order, payment
        |from tb1 join tb2 on tb1.province=tb2.province
        |""".stripMargin

    spark.

sql(sql2).show

    /*
    +--------+-----------+-------+
    |province|creat_order|payment|
    +--------+-----------+-------+
    |  浙江省|      14727|  14727|
    |  贵州省|       1557|   1557|
    |  广东省|        279|    279|
    |  上海市|      47652|  47651|
    |  江苏省|      20945|  20945|
    +--------+-----------+-------+
    */
//    +--------+-----------+-------+
//    |province|creat_order|payment|
//    +--------+-----------+-------+
//    |  浙江省|      44887|  44903|
//    |  贵州省|       4669|   4671|
//    |  广东省|        837|    837|
//    |  上海市|     142893| 142931|
//    |  江苏省|      62809|  62829|
//    +--------+-----------+-------+

// 从上面的输出可知，样本数据有点问题，计算的支付转换率都是100%
// 因此，人为制造不同的支付转化率(演示目的，比赛时的数据应当不会如此)
val grouped_order_df = spark.sql(sql2).withColumn("payment", col("payment") - (floor(rand() * 250) + 100))
grouped_order_df.show
    /*
    +--------+-----------+-------+
    |province|creat_order|payment|
    +--------+-----------+-------+
    |  浙江省|      14727|  14554|
    |  贵州省|       1557|   1451|
    |  广东省|        279|     12|
    |  上海市|      47652|  47394|
    |  江苏省|      20945|  20842|
    +--------+-----------+-------+
    */
//    +--------+-----------+-------+
//    |province|creat_order|payment|
//    +--------+-----------+-------+
//    |  浙江省|      44887|  44609|
//    |  贵州省|       4669|   4338|
//    |  广东省|        837|    565|
//    |  上海市|     142893| 142687|
//    |  江苏省|      62809|  62642|
//    +--------+-----------+-------+

// 统计支付转化率
val result_order_df = grouped_order_df.withColumn("payCVR", round(col("payment") / col("creat_order"), 3))
// 计算排名-使用窗口函数
val windowSpec = Window.orderBy(col("payCVR").desc)
val rank_order_df = result_order_df.withColumn("ranking", rank().over(windowSpec))

    rank_order_df.

show()
    /*
    +--------+-----------+-------+------+-------+
    |province|creat_order|payment|payCVR|ranking|
    +--------+-----------+-------+------+-------+
    |  上海市|      47652|  47394| 0.995|      1|
    |  江苏省|      20945|  20842| 0.995|      1|
    |  浙江省|      14727|  14554| 0.988|      3|
    |  贵州省|       1557|   1451| 0.932|      4|
    |  广东省|        279|     12| 0.043|      5|
    +--------+-----------+-------+------+-------+
    */
//    +--------+-----------+-------+------+-------+
//    |province|creat_order|payment|payCVR|ranking|
//    +--------+-----------+-------+------+-------+
//    |  上海市|     142893| 142687| 0.999|      1|
//    |  江苏省|      62809|  62642| 0.997|      2|
//    |  浙江省|      44887|  44609| 0.994|      3|
//    |  贵州省|       4669|   4338| 0.929|      4|
//    |  广东省|        837|    565| 0.675|      5|
//    +--------+-----------+-------+------+-------+

// 将计算结果写入clickhouse 的ds_result 库的payment_cvr 表
// 使用官方clickhouse驱动程序和连接信息
val ckUrl = "jdbc:clickhouse://192.168.45.13:8123/ds_result"
val ckDriver = "com.clickhouse.jdbc.ClickHouseDriver"
val ckUser = "default"
val ckPassword = "123456"
val ckTable = "ds_result.payment_cvr"
val props = new Properties
    props.

put("driver",ckDriver)
    props.

put("user",ckUser)
    props.

put("password",ckPassword)
    result_order_df.write.

mode("append").

jdbc(ckUrl, ckTable, props)
  }
      }
