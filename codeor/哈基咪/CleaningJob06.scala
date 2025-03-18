object CleaningJob06{
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
val spark = SparkSession.builder()
    .config("hive.exec.dynamic.partition", "true")
    .config("hive.exec.dynamic.partition.mode", "nonstrict")
    .config("spark.sql.sources.partitionOverwriteMode", "dynamic")
    .config("spark.sql.parquet.writeLegacyMode", "true")
    .config("spark.serializer", "org.apache.spark.sql.serializer.KryoSerializer")
    .enableHiveSupport()
    .getOrCreate()
val hbase_order_master = readHBase(spark)
    .withColumn("elt_date", lit("20241107"))
  }
def getScan:Scan ={
val scan = new Scan()
val rowFilter = new RowFilter(CompareOperator.EQUAL, new SubstringComparator("2024"))
    scan.

setFilter(rowFilter)
    scan.

withStartRow(Bytes.toBytes("0+20241108000000000"))
    scan.

withStopRow(Bytes.toBytes("0+20241108235959999"))
scan
  }

def readHBase(spark:SparkSession):DataFrame={
val hbaseConf = HBaseConfiguration.create()
    hbaseConf.

set("hbase.zookeeper.quorum","192.168.45.13")
    hbaseConf.

set("hbase.zookeeper.property.clientPort","2181")
    hbaseConf.

set("hbase.master","192.168.45.13:16000")
    hbaseConf.

set(TableInputFormat.INPUT_TABLE,"ods:order_master")
    hbaseConf.

set(TableInputFormat.SCAN, TableMapReduceUtil.convertScanToString(getScan))
val resultRDD:RDD[(ImmutableBytesWritable,Result)]=spark.sparkContext.

newAPIHadoopRDD(
    hbaseConf,
    classOf[TableInputFormat],
    classOf[ImmutableBytesWritable],
    classOf[Result]
)
    import spark.implicits._
    resultRDD.

map
{
    t =>
    val result = t._2
    val map:collection.mutable.Map[String, String]=collection.mutable.Map()
    for (cell < -result.rawCells())
    {
        val colum = Bytes.toString(CellUtil.cloneQualifier(cell))
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
    OrderMaster(order_id = map.get("order_id").get,    // Long
        order_sn = map.get("order_sn").get,
        customer_id = map.get("customer_id").get, // Long
        shipping_user = map.get("shipping_user").get,
        province = map.get("province").get,
        city = map.get("city").get,
        address = map.get("address").get,
        order_source = map.get("order_source").get,
        payment_method = map.get("payment_method").get,
        order_money = map.get("order_money").get,
        district_money = map.get("district_money").get,
        shipping_money = map.get("shipping_money").get,
        payment_money = map.get("payment_money").get,
        shipping_comp_name = map.get("shipping_comp_name").get,
        shipping_sn = map.get("shipping_sn").get,
        create_time = map.get("create_time").get,
        shipping_time = map.get("shipping_time").get,
        pay_time = map.get("pay_time").get,
        receive_time = map.get("receive_time").get,
        order_status = map.get("order_status").get,
        order_point = map.get("order_point").get,
        invoice_title = map.get("invoice_title").get,
        modified_time = map.get("modified_time").get
    )
}.toDF
  }
      }
