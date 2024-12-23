package _20241220

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.{CellUtil, CompareOperator}
import org.apache.hadoop.hbase.mapreduce._
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.util._
import org.apache.hadoop.hbase.filter._
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.{col, current_timestamp, date_trunc, lit}
import org.apache.spark.sql.types.DataTypes

import java.util

object qinxi4 {

    case class tableinfo(
                        order_id : String,
                        order_sn : String,
                        customer_id : String,
                        shipping_user  : String,
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

    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val sp = SparkSession.builder()
                .master("local[*]")
                .appName("ruawf")
                .config("hive.exec.scratchdir","hdfs://192.168.45.20:9000/user/temp/hive")
                .config("hive.exec.dynamic.partition.mode","nonstrict")
                .enableHiveSupport()
                .getOrCreate()

        val maxtime = sp.sql("select max(etl_date) from ods.order_master").first()(0)
        val hivetable = sp.sql(s"select * from ods.order_master where etl_date = '${maxtime}'")


        import sp.implicits._
        val config = new Configuration();
        config.set(TableInputFormat.INPUT_TABLE,"ods:order_master_offline")
        config.set(TableInputFormat.SCAN,TableMapReduceUtil.convertScanToString(getscan))
        config.set("hbase.zookeeper.quorum","192.168.45.20:2181")

        sp.sparkContext.newAPIHadoopRDD(
                    config,
                    classOf[TableInputFormat],
                    classOf[ImmutableBytesWritable],
                    classOf[Result])
                .map(f => {
                    val map = new util.HashMap[String, String]()
                    f._2.rawCells().foreach(e => {
                        val info = Bytes.toString(CellUtil.cloneQualifier(e))
                        val byvalue = CellUtil.cloneValue(e)
                        var str = ""
                        if (info == "order_id" || info == "customer_id" || info == "order_source" || info == "payment_method" || info == "order_point") {
                            str = Bytes.toLong(byvalue).toString
                        } else if (info == "order_money" || info == "district_money" || info == "shipping_money" || info == "payment_money") {
                            str = Bytes.toDouble(byvalue).toString
                        } else {
                            str = Bytes.toString(byvalue)
                        }
                        map.put(info, str)
                    })

                    tableinfo(
                        map.get("order_id"),
                        map.get("order_sn"),
                        map.get("customer_id"),
                        map.get("shipping_user "),
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

                .withColumn("order_id", col("order_id").cast(DataTypes.IntegerType))
                .withColumn("customer_id", col("customer_id").cast(DataTypes.IntegerType))
                .withColumn("order_source", col("order_source").cast(DataTypes.IntegerType))
                .withColumn("payment_method", col("payment_method").cast(DataTypes.IntegerType))
                .withColumn("order_point", col("order_point").cast(DataTypes.IntegerType))

                .withColumn("order_money", col("order_money").cast(DataTypes.createDecimalType(8, 2)))
                .withColumn("district_money", col("district_money").cast(DataTypes.createDecimalType(8, 2)))
                .withColumn("shipping_money", col("shipping_money").cast(DataTypes.createDecimalType(8, 2)))
                .withColumn("payment_money", col("payment_money").cast(DataTypes.createDecimalType(8, 2)))
                .withColumn("etl_date", lit(s"${maxtime.toString}"))


                .union(hivetable)
                .withColumn("dwd_insert_user", lit("user1"))
                .withColumn("dwd_insert_time", date_trunc("second", current_timestamp()))
                .withColumn("dwd_modify_user", lit("user1"))
                .withColumn("dwd_modify_time", date_trunc("second", current_timestamp()))
                .write
                .mode(SaveMode.Overwrite)
                .format("hive")
                .partitionBy("etl_date")
                .saveAsTable("dwd.fact_order_master")

    }

    def getscan ={
        val scan = new Scan

        val filter = new RowFilter(CompareOperator.EQUAL, new RegexStringComparator("20221001"))

        scan.setFilter(filter)

        scan.withStartRow(Bytes.toBytes("0+20221001000000000"))
        scan.withStopRow(Bytes.toBytes("0+20221001595959999"))

        scan

    }

}
