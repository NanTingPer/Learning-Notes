package Test03

import com.google.protobuf.CodedInputStream
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.{CellUtil, CompareOperator}
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.filter.{RegexStringComparator, RowFilter}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce._
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions.{col, current_timestamp, date_trunc, lit}
import org.apache.spark.sql.types.{DataTypes, DateType}

import java.time.LocalTime
import java.util

object qinxi03 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
       val sp = SparkSession.builder()
               .appName("daf")
               .master("local[*]")
               .config("hive.exec.scratchdir","hdfs://192.168.45.13:9000/user/temp/hive")
               .config("hive.exec.dynamic.partition.mode","nonstrict")
               .enableHiveSupport()
               .getOrCreate()

        val mtime = sp.sql("select max(etl_date) from ods.order_master").first()(0)
        println(mtime)
        val hivedata = sp.sql("select * from ods.order_master")

        val con = new Configuration()
        con.set(TableInputFormat.INPUT_TABLE,"ods:order_master_offline")
        con.set(TableInputFormat.SCAN,TableMapReduceUtil.convertScanToString(getScan()))
        con.set("hbase.zookeeper.quorum","192.168.45.13:2181")

        import sp.implicits._

        sp.sparkContext.newAPIHadoopRDD(
            con,
            classOf[TableInputFormat],
            classOf[ImmutableBytesWritable],
            classOf[Result])
                .map(f => {
                    val value = f._2

                    val map = new util.HashMap[String, String]()

                    value.rawCells().foreach(f2 => {
                        val rowinfo = CellUtil.cloneQualifier(f2)
                        val xxxxvalue = CellUtil.cloneValue(f2)
                        var rowname = Bytes.toString(rowinfo)
                        var rowvalue = ""
                        if(rowname == "order_id" || rowname == "order_source" || rowname == "payment_method" || rowname == "order_point"){
                            rowvalue = Bytes.toLong(xxxxvalue).toString
                        }else if(rowname == "order_money" || rowname == "district_money" || rowname == "shipping_money" || rowname == "payment_money"){
                            rowvalue = Bytes.toDouble(xxxxvalue).toString
                        }else{
                            rowvalue = Bytes.toString(xxxxvalue)
                        }
                        map.put(rowname,rowvalue)
                    })

                    tableInfo(
                        map.get("order_id"),
                        map.get("order_sn"),
                        map.get("customer_id"),
                        map.get("shipping_user"),
                        map.get("province"),
                        map.get("city"),
                        map.get("address"),
                        map.get("order_source"),
                        map.get("payment_method"),
                        map.get("order_money"),
                        map.get("district_money"),
                        map.get("shipping_money"),
                        map.get("payment_money"),
                        map.get("shipping_comp_name"),
                        map.get("shipping_sn"),
                        map.get("create_time"),
                        map.get("shipping_time"),
                        map.get("pay_time"),
                        map.get("receive_time"),
                        map.get("order_status"),
                        map.get("order_point"),
                        map.get("invoice_title"),
                        map.get("modified_time")
                    )
                }).toDF()
                .withColumn("order_id",col("order_id").cast(DataTypes.IntegerType))
                .withColumn("customer_id",col("customer_id").cast(DataTypes.IntegerType))
                .withColumn("order_source",col("order_source").cast(DataTypes.IntegerType))
                .withColumn("payment_method",col("payment_method").cast(DataTypes.IntegerType))
                .withColumn("order_point",col("order_point").cast(DataTypes.IntegerType))

                .withColumn("order_money",col("order_money").cast(DataTypes.DoubleType))
                .withColumn("district_money",col("district_money").cast(DataTypes.DoubleType))
                .withColumn("shipping_money",col("shipping_money").cast(DataTypes.DoubleType))
                .withColumn("payment_money",col("payment_money").cast(DataTypes.DoubleType))
                .withColumn("etl_date",lit("20241220"/*s"${mtime.toString}"*/))

                .union(hivedata)

                .withColumn("dwd_insert_user",lit("user1"))
                .withColumn("dwd_insert_time",date_trunc("second",current_timestamp()))
                .withColumn("dwd_modify_user",lit("user1"))
                .withColumn("dwd_modify_time",date_trunc("second",current_timestamp()))

                .write
                .mode(SaveMode.Overwrite)
                .partitionBy("etl_date")
                .format("hive")
                .saveAsTable("dwd.fact_order_master")

    }

    def getScan()={
        val scan = new Scan

        new RowFilter(CompareOperator.EQUAL,new RegexStringComparator("20221001"))

        scan.withStartRow(Bytes.toBytes("0+20221001000000000"))
        scan.withStopRow(Bytes.toBytes("9+20221001245959999"))

        scan


    }

    case class tableInfo(
                         order_id : String,
                         order_sn : String,
                         customer_id : String,
                         shipping_user : String,
                         province : String,
                         city : String,
                         address : String,
                         order_source : String,
                         payment_method : String,
                         order_money : String,
                         district_money : String,
                         shipping_money : String,
                         payment_money : String,
                         shipping_comp_name : String,
                         shipping_sn : String,
                         create_time : String,
                         shipping_time : String,
                         pay_time : String,
                         receive_time : String,
                         order_status : String,
                         order_point : String,
                         invoice_title : String,
                         modified_time : String
                        )

}

