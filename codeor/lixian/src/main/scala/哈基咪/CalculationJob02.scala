object CalculationJob02{

def main(args:Array[String]):Unit ={

// 创建SparkSession实例
val spark = SparkSession.builder()
    .master("local[*]")
    .appName("Data Calculation")
    // 兼容Hive Parquet存储格式
    .config("spark.sql.parquet.writeLegacyFormat", "true")
    .enableHiveSupport()
    .getOrCreate()

// 加载order_master表中已付款的订单记录
val paymented_orders_df = spark.sql("select order_sn from dwd.fact_order_master where order_status='已付款'")

// 加载order_master表中已退款的订单记录
val refunded_orders_df = spark.sql("select order_sn from dwd.fact_order_master where order_status='已退款'")

// 从"已付款"订单中剔除"已退款"的订单
val valid_order_df = paymented_orders_df.except(refunded_orders_df)

// 从fact_order_detail中查询出所有已付款订单的明细
val valid_order_detail_df = spark
    .table("dwd.fact_order_detail")
    .select("order_sn", "product_id", "product_cnt", "product_price")
    .join(valid_order_df, "order_sn")   // 关联到已付款订单编号

// 计算每个商品的小计金额：product_cnt * product_price。
// 以及每个商品的累计销售总金额和销售总数量
val product_sales = valid_order_detail_df
    .withColumn("product_subtotal", col("product_cnt") * col("product_price"))
    .groupBy("product_id")
    .agg(sum("product_subtotal").as("sales_amount"), sum("product_cnt").as("product_totalcnt"))

// 定义窗口规范
val windowSpec = Window.orderBy(col("sales_amount").desc)

// 按销售金额进行排名
val product_sales_rank = product_sales.withColumn("sales_rank", rank().over(windowSpec))
    product_sales_rank.

show()
    /*
    +----------+------------+----------------+----------+
    |product_id|sales_amount|product_totalcnt|sales_rank|
    +----------+------------+----------------+----------+
    |       599|    77042.24|              25|         1|
    |     11976|    66334.68|              20|         2|
    |      9193|    63572.52|              21|         3|
    |      9344|    63331.29|              21|         4|
    |      8048|    62307.63|              21|         5|
    |      7138|    62303.85|              17|         6|
    |       644|    61944.12|              18|         7|
    |      7122|    61841.95|              19|         8|
    |      8095|    61794.16|              17|         9|
    |      1083|    61700.94|              18|        10|
    |       889|    61529.16|              18|        11|
    |      1250|    61260.80|              19|        12|
    |     12609|    60857.26|              18|        13|
    |      5437|    60768.68|              19|        14|
    |     12483|    60382.08|              19|        15|
    |      7489|    59980.20|              17|        16|
    |     13563|    59397.51|              19|        17|
    |       651|    59235.60|              16|        18|
    |     14444|    58651.45|              19|        19|
    |      6558|    58593.60|              20|        20|
    +----------+------------+----------------+----------+
    only showing top 20 rows
    */

// 将统计结果写入clickhouse
// 使用官方clickhouse驱动程序和连接信息
val ckUrl = "jdbc:clickhouse://192.168.45.13:8123/ds_result"  // 数据库连接url
val ckDriver = "com.clickhouse.jdbc.ClickHouseDriver" // 驱动程序
val ckUser = "default"
val ckPassword = "123456"
val ckTable = "ds_result.sales_amount_rank"

val props = new Properties
    props.

put("driver",ckDriver)
    props.

put("user",ckUser)
    props.

put("password",ckPassword)
    product_sales_rank.write.

mode("append").

jdbc(ckUrl, ckTable, props)
  }
      }
