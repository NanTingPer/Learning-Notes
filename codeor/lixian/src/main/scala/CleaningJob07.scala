import org.apache.hadoop.hbase.filter._
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.hadoop.hbase.CompareOperator
import org.apache.hadoop.hbase.{HBaseConfiguration, _}
import org.apache.hadoop.hbase.mapreduce._
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.spark.rdd.RDD

object CleaningJob07{
  case class Order_Detail(
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
  def main(args:Array[String]):Unit={
    val spark = SparkSession.builder()
      .master("local[*]")
      .appName("ETL CLAN")
      .enableHiveSupport()
      .getOrCreate()
//    val hbase_order_detail = readHBase(spark)
//      .withcolumn("elt_date",lit("20241108"))
  }
  def getScan:Scan={
    val scan = new Scan()
    val rowFilter = new RowFilter(CompareOperator.EQUAL,new SubstringComparator("20241108"))
    scan.setFilter(rowFilter)
    scan.withStartRow(Bytes.toBytes("0+20241108000000000"))
    scan.withStopRow(Bytes.toBytes("9+20241108235959999"))
  }
  def readHBase(spark:SparkSession):DataFrame={
    val hbaseConf = HBaseConfiguration.create()
    hbaseConf.set("hbase.zookeeper.quorum","192.168.45.13")
    hbaseConf.set("hbase.zookeeper.property.clientPort","2181")
    hbaseConf.set("hbase.master","192.168.45.13:16000")
    hbaseConf.set(TableInputFormat.INPUT_TABLE,"ods:order_detail")
    hbaseConf.set(TableInputFormat.SCAN,TableMapReduceUtil.convertScanToString(getScan))
    val resultRDD:RDD[(ImmutableBytesWritable,Result)]=spark.sparkContext.newAPIHadoopRDD(
      hbaseConf,
      classOf[TableInputFormat],
      classOf[ImmutableBytesWritable],
      classOf[Result]
    )
    import spark.implicits._
    resultRDD.map{t=>
      val result = t._2
      val map:collection.mutable.Map[String,String]=collection.mutable.Map()
      for(cell<-result.rawCells()){
        val column = Bytes.toString(CellUtil.cloneQualifier(cell))
        val value = Bytes.toString(CellUtil.cloneValue(cell))
        map.put(column,value)
      }
      Order_Detail(
        order_detail_id=map("order_detail_id"),
        order_sn=map("order_detail_id"),
        product_id=map("order_detail_id"),
        product_name=map("order_detail_id"),
        product_cnt=map("order_detail_id"),
        product_price=map("order_detail_id"),
        average_cost=map("order_detail_id"),
        weight=map("order_detail_id"),
        fee_money=map("order_detail_id"),
        w_id=map("order_detail_id"),
        create_time=map("order_detail_id"),
        modified_time=map("order_detail_id")
      )
    }
      .toDF()
  }
}