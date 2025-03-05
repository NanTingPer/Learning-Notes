object CleaningJob05{

// case class
private case

class OrderDetail(
    order_detail_id:String,
    order_sn:String,
    product_id:String,
    product_name:String,
    product_cnt:String,
    product_price:String,
    average_cost:String,
    weight:String,
    fee_money:String,
    w_id:String,
    create_time:String,
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
val hive_ods_order_detail = spark.sql("select * from ods.order_detail")

// 2）从HBase表中读取数据到DataFrame中
    /*
     需转换的列：
     - String => Long：order_detail_id, product_id, product_cnt, w_id
     - String => Float： weight
     - String => Decimal(8,2)：product_price, average_cost, fee_money
     - String => Timestamp：modified_time
     - 增加分区列：etl_date="20231225"
    */
val hbase_ods_order_detail = readHBase(spark)
    // String => Long
    .withColumn("order_detail_id", col("order_detail_id").cast("long"))
    .withColumn("product_id", col("product_id").cast("long"))
    .withColumn("product_cnt", col("product_cnt").cast("long"))
    .withColumn("w_id", col("w_id").cast("long"))
    // String => Float
    .withColumn("weight", col("weight").cast("float"))
    // String => Decimal(8,2)
    .withColumn("product_price", col("product_price").cast("decimal(8,2)"))
    .withColumn("average_cost", col("average_cost").cast("decimal(8,2)"))
    .withColumn("fee_money", col("fee_money").cast("decimal(8,2)"))
    // String => Timestamp
    .withColumn("modified_time", col("modified_time").cast("timestamp"))
    // 增加分区列
    .withColumn("etl_date", lit("20231225"))
// 3）合并存量数据和增量数据，写入Hive DWD层
val fact_order_detail = hive_ods_order_detail
    // 垂直合并
    .union(hbase_ods_order_detail)
    // 增加新的列
    .withColumn("dwd_insert_user", lit("user1"))
    .withColumn("dwd_insert_time", date_trunc("second", current_timestamp()))     // spark 2.3.0
    .withColumn("dwd_modify_user", lit("user1"))
    .withColumn("dwd_modify_time", date_trunc("second", current_timestamp()))     // spark 2.3.0

// 4）写入Hive DWD层
// 如果已存在有fact_order_detail表，则与前面的示例相同，使用InsertInto()方法
// 这里我们假设没有fact_order_detail表，使用saveAsTable()方法
println("正在写入 Hive DWD层 ...")
    fact_order_detail.write
        .

format("hive")
      .

mode("overwrite")      // 覆盖
      .

partitionBy("etl_date")    // 指定分区
      .

saveAsTable("dwd.fact_order_detail") // 保存到Hive表

println("已完成写入 Hive DWD层")
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
val rowFilter = new RowFilter(CompareOperator.EQUAL, new SubstringComparator("20221001"))
// scan 设置过滤器
    scan.

setFilter(rowFilter)

    scan.

withStartRow(Bytes.toBytes("0+20221001000000000"))   // 起始行键
    scan.

withStopRow(Bytes.toBytes("9+20221001235959999"))    // 结束行键

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
val tablename = "ods:order_detail" // 要读取的hbase表名
    hbaseConf.

set(TableInputFormat.INPUT_TABLE, tablename)  // 注意：读取时使用TableInputFormat

// 2）添加filter => TableMapReduceUtil.convertScanToString(getScan)
//    hbaseConf.set(TableInputFormat.SCAN, TableMapReduceUtil.convertScanToString(getScan))

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
        if (colum == "product_cnt" || colum == "w_id")
        {
            val value = Bytes.toInt(CellUtil.cloneValue(cell))
            map.put(colum, value.toString)
        } else if (colum == "order_detail_id" || colum == "product_id")
        {
            val value = Bytes.toLong(CellUtil.cloneValue(cell))
            map.put(colum, value.toString)
        } else if (colum == "product_price" || colum == "average_cost" || colum == "weight" || colum == "fee_money")
        {
            val value = Bytes.toDouble(CellUtil.cloneValue(cell))
            map.put(colum, value.toString)
        } else
        {
            val value = Bytes.toString(CellUtil.cloneValue(cell))
            map.put(colum, value)
        }
    }
    // 构造为OrderDetail对象实例
    OrderDetail(order_detail_id = map("order_detail_id"),
        order_sn = map("order_sn"),
        product_id = map("product_id"),
        product_name = map("product_name"),
        product_cnt = map("product_cnt"),
        product_price = map("product_price"),
        average_cost = map("average_cost"),
        weight = map("weight"),
        fee_money = map("fee_money"),
        w_id = map("w_id"),
        create_time = map("create_time"),
        modified_time = map("modified_time")
    )
}
// 转换为DataFrame
      .

toDF()
  }
      }
