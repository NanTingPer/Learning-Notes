package Test01

import org.apache.hadoop.hbase.{CellUtil, CompareOperator, HBaseConfiguration}
import org.apache.hadoop.hbase.client.{Result, Scan}
import org.apache.hadoop.hbase.filter.{RegexStringComparator, RowFilter}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.{TableInputFormat, TableMapReduceUtil}
import org.apache.hadoop.hbase.protobuf.generated.ComparatorProtos.ByteArrayComparable
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions.{col, current_timestamp, date_trunc, lit}
import org.apache.spark.sql.types.DataTypes

import java.time.LocalTime
import java.util

//抽取ods 库中表order_detail 表最新分区的数据，
// 并结合HBase 中order_detail_offline
// 表中的数据合并抽取到dwd 库中fact_order_detail的分区表，
//
// 分区字段为etl_date 且值与ods 库的相对应表该值相等，
// 并添加
// dwd_insert_user、
// dwd_insert_time、
// dwd_modify_user、
// dwd_modify_time四列，
// 其中dwd_insert_user 、dwd_modify_user 均填写“ user1 ” ，

// dwd_insert_time、dwd_modify_time
// 均填写当前操作时间（年月日必须是今天，时分秒只需在比赛时间范围内即可），
//
// 抽取HBase 中的数据时，只抽取2022 年10 月01 日的数据（以rowkey 为准），
// 并进行数据类型转换。
//
// 使用hive cli 查询modified_time 为2022 年10 月01 日当天的数据，
// 查询字段为order_detail_id 、order_sn 、product_name 、create_time ，
// 并按照order_detail_id 进行升序排序，
//
// 将结果截图粘贴至客户端桌面【Release\模块D 提交结果.docx】中对应的任务序号下；

object 离线清洗05 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession.builder()
                .enableHiveSupport()
                .master("local[*]")
                .appName("spark05")
                .config("hive.exec.scratchdir","hdfs://192.168.45.13:9000/user/hive/temp")
                .config("hive.exec.dynamic.partition.mode","nonstrict")
                .getOrCreate()

        val mtime = spark.sql("select max(etl_date) from ods.order_detail").first()(0)
        val odsdata = spark.sql(s"select * from ods.order_detail where etl_date = '${mtime}'")


        //HBase
        val hconfig = HBaseConfiguration.create()
        hconfig.set(TableInputFormat.INPUT_TABLE,"ods:fact_order_detail")
        hconfig.set(TableInputFormat.SCAN,TableMapReduceUtil.convertScanToString(getScan))

        val hdata = spark.sparkContext.newAPIHadoopRDD(
            hconfig,
            classOf[TableInputFormat],
            classOf[ImmutableBytesWritable],
            classOf[Result])

        import spark.implicits._
        hdata.map(f => {
            val cells = f._2.rawCells()

            val map = new util.HashMap[String, String]()

            cells.foreach(f => {
                val info = Bytes.toString(CellUtil.cloneQualifier(f))
                val value = CellUtil.cloneValue(f)
                var fvalue = ""
                if (info == "order_detail_id" || info == "product_id" || info == "product_cnt" || info == "w_id") {
                    fvalue = Bytes.toLong(value).toString
                } else if (info == "product_price" || info == "average_cost" || info == "weight" || info == "fee_money") {
                    fvalue = Bytes.toDouble(value).toString
                }
                else {
                    fvalue = Bytes.toString(value)
                }
                map.put(info, fvalue)
            })

            new tableinfo(
                map.get("order_detail_id"),
                map.get("order_sn"),
                map.get("product_id"),
                map.get("product_name"),
                map.get("product_cnt"),
                map.get("product_price"),
                map.get("average_cost"),
                map.get("weight"),
                map.get("fee_money"),
                map.get("w_id"),
                map.get("create_time"),
                map.get("modified_time"))
        })
        .toDF()
                .withColumn("order_detail_id",col("order_detail_id").cast(DataTypes.IntegerType))
                .withColumn("product_id",col("product_id").cast(DataTypes.IntegerType))
                .withColumn("product_cnt",col("product_cnt").cast(DataTypes.IntegerType))
                .withColumn("w_id",col("w_id").cast(DataTypes.IntegerType))

                .withColumn("product_price",col("product_price").cast(DataTypes.createDecimalType(8,2)))
                .withColumn("average_cost",col("average_cost").cast(DataTypes.createDecimalType(8,2)))
                .withColumn("weight",col("weight").cast(DataTypes.createDecimalType(8,2)))
                .withColumn("fee_money",col("fee_money").cast(DataTypes.createDecimalType(8,2)))
                .withColumn("etl_date",lit("20111111"))

                .union(odsdata)
                .withColumn("dwd_insert_user",lit("user1"))
                .withColumn("dwd_insert_time",date_trunc("second",current_timestamp()))
                .withColumn("dwd_modify_user",lit("user1"))
                .withColumn("dwd_modify_time",date_trunc("second",current_timestamp()))

                .write
                .mode(SaveMode.Overwrite)
                .format("hive")
                .partitionBy("etl_date")
                .saveAsTable("dwd.fact_order_detail")
    }

    def getScan ={
        val scan = new Scan

        val filter = new RowFilter(CompareOperator.EQUAL,new RegexStringComparator("20241212"))

        scan.withStartRow(Bytes.toBytes("0+20241212000000000"))
        scan.withStopRow(Bytes.toBytes("9+20241212245959999"))
        scan.setFilter(filter);

        scan
    }

    case class tableinfo(
                        order_detail_id :String,
                        order_sn :String,
                        product_id : String,
                        product_name : String,
                        product_cnt : String,
                        product_price : String,
                        average_cost : String,
                        weight : String,
                        fee_money : String,
                        w_id :String,
                        create_time : String,
                        modified_time : String
                        )
}
