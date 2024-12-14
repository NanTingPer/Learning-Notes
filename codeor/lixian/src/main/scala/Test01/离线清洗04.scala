package Test01

import com.google.protobuf.CodedInputStream
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.{CellUtil, CompareOperator, HBaseConfiguration}
import org.apache.hadoop.hbase.client.{Result, Scan}
import org.apache.hadoop.hbase.filter.{Filter, RegexStringComparator, RowFilter, SubstringComparator}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.{TableInputFormat, TableMapReduceUtil}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions.{col, current_timestamp, date_trunc, lit}
import org.apache.spark.sql.types.DataTypes

import java.time.LocalTime
import java.util

object 离线清洗04 {
    case class table_info(order_id : String,
                          order_sn : String,
                          customer_id :String,
                          shipping_user : String,
                          province:String,
                          city:String,
                          address:String,
                          order_source :String,
                          payment_method :String,
                          order_money : String,
                          district_money :String,
                          shipping_money :String,
                          payment_money :String,
                          shipping_comp_name :String,
                          shipping_sn :String,
                          create_time :String,
                          shipping_time :String,
                          pay_time :String,
                          receive_time : String,
                          order_status:String,
                          order_point : String,
                          invoice_title :String,
                          modified_time :String)


    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession.builder()
                .enableHiveSupport()
                .master("local[*]")
                .appName("spark4")
                .config("hive.exec.scratchdir","hdfs://192.168.45.13:9000/user/hive/temp")
                .config("hive.exec.dynamic.partition.mode","nonstrict")
                .getOrCreate()

        //TODO 获取ods order_master 中最新数据
        val maxTime = spark.sql("select max(etl_date) from ods.order_master").first()(0)
        val odsData = spark.sql(s"select * from ods.order_master where etl_date = '${maxTime}'")


        //TODO 获取HBase 数据
        val config = HBaseConfiguration.create()
        config.set(TableInputFormat.INPUT_TABLE,"ods:order_master")
        config.set(TableInputFormat.SCAN,TableMapReduceUtil.convertScanToString(GetScan))

        val rdd = spark.sparkContext.newAPIHadoopRDD(config,
            classOf[TableInputFormat],
            classOf[ImmutableBytesWritable],
            classOf[Result])

        //TODO 过滤并转换
        import spark.implicits._

        val hbaseDf = rdd.map(f => {
            val results = f._2
            val tableInfo = new util.HashMap[String, String]()
            results.rawCells().foreach(cell => {
                //TODO info
                val info = Bytes.toString(CellUtil.cloneQualifier(cell))
                //TODO value
                var value = ""
                if(info.equals("order_id") || info.equals("customer_id") || info.equals("order_source") || info.equals("payment_method") || info.equals("order_point")){
                    value = Bytes.toLong(CellUtil.cloneValue(cell)).toString
                }else if(info.equals("order_money") || info.equals("district_money") || info.equals("shipping_money") || info.equals("payment_money")){
                    value = Bytes.toDouble(CellUtil.cloneValue(cell)).toString
                }else{
                    value = Bytes.toString(CellUtil.cloneValue(cell))
                }

                tableInfo.put(info,value)

            })
            new table_info(
                tableInfo.get("order_id"),
                tableInfo.get("order_sn"),
                tableInfo.get("customer_id"),
                tableInfo.get("shipping_user"),
                tableInfo.get("province"),
                tableInfo.get("city"),
                tableInfo.get("address"),
                tableInfo.get("order_source"),
                tableInfo.get("payment_method"),
                tableInfo.get("order_money"),
                tableInfo.get("district_money"),
                tableInfo.get("shipping_money"),
                tableInfo.get("payment_money"),
                tableInfo.get("shipping_comp_name"),
                tableInfo.get("shipping_sn"),
                tableInfo.get("create_time"),
                tableInfo.get("shipping_time"),
                tableInfo.get("pay_time"),
                tableInfo.get("receive_time"),
                tableInfo.get("order_status"),
                tableInfo.get("order_point"),
                tableInfo.get("invoice_title"),
                tableInfo.get("modified_time")
            )

        }).toDF()

        hbaseDf.withColumn("order_id",col("order_id").cast(DataTypes.IntegerType))
                .withColumn("customer_id",col("customer_id").cast(DataTypes.IntegerType))
                .withColumn("order_source",col("order_source").cast(DataTypes.IntegerType))
                .withColumn("payment_money",col("payment_money").cast(DataTypes.IntegerType))
                .withColumn("order_point",col("order_point").cast(DataTypes.IntegerType))

                .withColumn("order_money",col("order_money").cast(DataTypes.createDecimalType(8,2)))
                .withColumn("district_money",col("district_money").cast(DataTypes.createDecimalType(8,2)))
                .withColumn("shipping_money",col("shipping_money").cast(DataTypes.createDecimalType(8,2)))
                .withColumn("payment_money",col("payment_money").cast(DataTypes.createDecimalType(8,2)))

                .withColumn("modified_time",col("modified_time").cast(DataTypes.TimestampType))
                .withColumn("etl_date",lit("20231225"))

                .union(odsData)
                .withColumn("dwd_insert_user",lit("user1"))
                .withColumn("dwd_insert_time",lit(date_trunc("second",current_timestamp())))
                .withColumn("dwd_modify_user",lit("user1"))
                .withColumn("dwd_modify_time",lit(date_trunc("second",current_timestamp())))

                .write
                .format("hive")
                .mode(SaveMode.Overwrite)
                .partitionBy("etl_date")
                .saveAsTable("dwd.fact_order_master")
    }

    //TODO 构建获取scan的方法
    def GetScan ={
        val scan = new Scan
        val filter = new RowFilter(CompareOperator.EQUAL, new RegexStringComparator("2024"))
        scan.setFilter(filter)

        scan.withStartRow(Bytes.toBytes("0+20221001000000000"))
        scan.withStopRow(Bytes.toBytes("9+20221001235959999"))

        scan

    }
}
