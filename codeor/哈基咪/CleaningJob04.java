object CleaningJob04{

    // case class
    case

class OrderMaster(
    order_id:String,    // Long
    order_sn:String,
    customer_id:String, // Long
    shipping_user:String,
    province:String,
    city:String,
    address:String,
    order_source:String,
    payment_method:String,
    order_money:String,
    district_money:String,
    shipping_money:String,
    payment_money:String,
    shipping_comp_name:String,
    shipping_sn:String,
    create_time:String,
    shipping_time:String,
    pay_time:String,
    receive_time:String,
    order_status:String,
    order_point:String,
    invoice_title:String,
    modified_time:String
)

  def main(args:Array[String]):Unit ={

// 创建SparkSession实例
val spark = SparkSession.builder()
    .master("local[*]")
    .appName("Data Clean")
    // 兼容Hive Parquet存储格式
    .config("spark.sql.parquet.writeLegacyFormat", "true")
    // 打开Hive动态分区的标志
    .config("hive.exec.dynamic.partition", "true")
    .config("hive.exec.dynamic.partition.mode", "constrict")
    // 启用动态分区覆盖模式（根据分区值，覆盖原来的分区）
    .config("spark.sql.sources.partitionOverwriteMode", "dynamic")
    .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    .enableHiveSupport()
    .getOrCreate()

// 1）从Hive ODS读取离线订单数据
val hive_ods_order_master = spark.sql("select * from ods.order_master")

// 2）从HBase表中读取数据到DataFrame中
    /*
     需转换的列：
     - String => Long：order_id, customer_id, order_point
     - String => Int： order_source, payment_method
     - String => Decimal(8,2)：order_money, district_money, shipping_money, payment_money
     - String => Timestamp：modified_time
     - 增加分区列：etl_date="20231225"
    */
val hbase_ods_order_master = readHBase(spark)
//      .drop("key")
// String => Long
//      .withColumn("order_id", col("order_id").cast("long"))
//      .withColumn("customer_id", col("customer_id").cast("long"))
//      .withColumn("order_point", col("order_point").cast("long"))
//      // String => Int
//      .withColumn("order_source", col("order_source").cast("int"))
//      .withColumn("payment_method", col("payment_method").cast("int"))
//      // String => Decimal(8,2)
//      .withColumn("order_money", col("order_money").cast("decimal(8,2)"))
//      .withColumn("district_money", col("district_money").cast("decimal(8,2)"))
//      .withColumn("shipping_money", col("shipping_money").cast("decimal(8,2)"))
//      .withColumn("payment_money", col("payment_money").cast("decimal(8,2)"))
//      // String => Timestamp
//      .withColumn("modified_time", col("modified_time").cast("timestamp"))
//      // 增加分区列
//      .withColumn("etl_date",lit("20231225"))

    hbase_ods_order_master.

show()
// 3）合并存量数据和增量数据，写入Hive DWD层CleaningJob04$
//    val fact_order_master = hive_ods_order_master
//      // 垂直合并
//      .union(hbase_ods_order_master)
//      // 增加新的列
//      .withColumn("dwd_insert_user",lit("user1"))
//      .withColumn("dwd_insert_time",date_trunc("second",current_timestamp()))     // spark 2.3.0
//      .withColumn("dwd_modify_user",lit("user1"))
//      .withColumn("dwd_modify_time",date_trunc("second",current_timestamp()))     // spark 2.3.0
//
//    // 4）写入Hive DWD层
//    // 如果已存在有fact_order_master表，则与前面的示例相同，使用InsertInto()方法
//    // 这里我们假设没有fact_order_master表，使用saveAsTable()方法
//     println("正在写入 Hive DWD层 ...")
//    fact_order_master.write
//      .format("hive")
//      .mode("overwrite")      // 覆盖
//      .partitionBy("etl_date")    // 指定分区
//      .saveAsTable("dwd.fact_order_master") // 保存到Hive表
//
//    println("已完成写入 Hive DWD层")
  }

/*  创建scan过滤, 过滤出rowkey包含'20221001'内容的行
 * 假设rowkey格式为：创建日期_发布日期_ID_TITLE
 * 目标：查找  rowkey中包含 20221001 的数据
 * 使用行过滤器：new RowFilter(CompareOp.EQUAL , new SubstringComparator("20221001"))
 * 返回 org.apache.hadoop.hbase.client.Scan
 */
def getScan:Scan ={
// 创建Scan
val scan = new Scan()
// 指定行过滤器（行键必须包含子字符串"20221001"）
val rowFilter = new RowFilter(CompareOperator.EQUAL, new RegexStringComparator("2024"))
// scan 设置过滤器
    scan.

setFilter(rowFilter)
    scan.

withStartRow(Bytes.toBytes("0+20241001000000000"))   // 起始行键
    scan.

withStopRow(Bytes.toBytes("9+20241001235959999"))    // 结束行键

scan
  }

// 自定义方法：使用 newAPIHadoopRDD 读取数据
def readHBase(spark:SparkSession):DataFrame ={

// 1) 创建HbaseConfiguration配置信息
val hbaseConf = HBaseConfiguration.create()
    hbaseConf.

set("hbase.zookeeper.quorum","192.168.45.13") //设置zooKeeper集群地址，也可以通过将hbase-site.xml拷贝到resources目录下
    hbaseConf.

set("hbase.zookeeper.property.clientPort","2181") //设置zookeeper连接端口，默认2181
    hbaseConf.

set("hbase.master","192.168.45.13:16000")     // 设置HBase HMaster

// 设置从HBase哪张表读取数据
val tablename = "ods:order_master" // 要读取的hbase表名
    hbaseConf.

set(TableInputFormat.INPUT_TABLE, tablename)  // 注意：读取时使用TableInputFormat

// 2）添加filter => TableMapReduceUtil.convertScanToString(getScan)
    hbaseConf.

set(TableInputFormat.SCAN, TableMapReduceUtil.convertScanToString(getScan))

// 3）调用SparkContext中newAPIHadoopRDD读取表中的数据，构建RDD
val resultRDD:RDD[(ImmutableBytesWritable,Result)]=spark.sparkContext.

newAPIHadoopRDD(
    hbaseConf,
    classOf[TableInputFormat],
    classOf[ImmutableBytesWritable],
    classOf[Result]
)

// 4）对获取的值进行过滤，并转换为DataFrame返回
    import
spark.implicits._
    resultRDD
      .

map
{
    t =>
    val result = t._2  // 获得Result
    val map:collection.mutable.Map[String, String] =collection.mutable.Map()  // 存储列限定符和列值
    for (cell < -result.rawCells())
    {
        val colum = Bytes.toString(CellUtil.cloneQualifier(cell)) // 列限定符
        if (colum == "order_money" || colum == "district_money"
            || colum == "shipping_money" || colum == "payment_money")
        {
            val value = Bytes.toDouble(CellUtil.cloneValue(cell))
            map.put(colum, value.toString)
        } else if (colum == "order_id" || colum == "customer_id")
        {
            val value = Bytes.toLong(CellUtil.cloneValue(cell))
            map.put(colum, value.toString)
        } else if (colum == "order_source" || colum == "order_point"
            || colum == "payment_method")
        {
            val value = Bytes.toInt(CellUtil.cloneValue(cell))
            map.put(colum, value.toString)
        } else
        {
            val value = Bytes.toString(CellUtil.cloneValue(cell))
            map.put(colum, value)
        }
    }
    // 构造为OrderMaster对象实例
    OrderMaster(order_id = map("order_id"),
        order_sn = map("order_sn"),
        customer_id = map("customer_id"),
        shipping_user = map("shipping_user"),
        province = map("province"),
        city = map("city"),
        address = map("address"),
        order_source = map("order_source"),
        payment_method = map("payment_method"),
        order_money = map("order_money"),
        district_money = map("district_money"),
        shipping_money = map("shipping_money"),
        payment_money = map("payment_money"),
        shipping_comp_name = map("shipping_comp_name"),
        shipping_sn = map("shipping_sn"),
        create_time = map("create_time"),
        shipping_time = map("shipping_time"),
        pay_time = map("pay_time"),
        receive_time = map("receive_time"),
        order_status = map("order_status"),
        order_point = map("order_point"),
        invoice_title = map("invoice_title"),
        modified_time = map("modified_time")
    )
}
// 转换为DataFrame
      .

toDF()
  }
      }
