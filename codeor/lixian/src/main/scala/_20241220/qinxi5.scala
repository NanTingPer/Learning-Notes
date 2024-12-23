package _20241220

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.filter._
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce._
import org.apache.hadoop.hbase.util._
import org.apache.hadoop.hbase.{CellUtil, CompareOperator}
import org.apache.spark.sql.functions.{col, current_timestamp, date_trunc, lit}
import org.apache.spark.sql.types.DataTypes
import org.apache.spark.sql.{SaveMode, SparkSession}

import java.util

object qinxi5 {

    case class tableinfo(
                        order_detail_id : String,
                        order_sn : String,
                        product_id : String,
                        product_name : String,
                        product_cnt : String,
                        product_price : String,
                        average_cost : String,
                        weight : String,
                        fee_money : String,
                        w_id : String,
                        create_time : String,
                        modified_time : String)

    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val sp = SparkSession.builder()
                .master("local[*]")
                .appName("ruawf")
                .config("hive.exec.scratchdir","hdfs://192.168.45.20:9000/user/temp/hive")
                .config("hive.exec.dynamic.partition.mode","nonstrict")
                .enableHiveSupport()
                .getOrCreate()

        val maxtime = sp.sql("select max(etl_date) from ods.order_detail").first()(0)
        val hivetable = sp.sql(s"select * from ods.order_detail where etl_date = '${maxtime}'")


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
                        if (info == "order_detail_id" || info == "product_id" || info == "product_cnt" || info == "w_id") {
                            str = Bytes.toLong(byvalue).toString
                        } else if (info == "product_price" || info == "average_cost" || info == "weight" || info == "fee_money") {
                            str = Bytes.toDouble(byvalue).toString
                        } else {
                            str = Bytes.toString(byvalue)
                        }
                        map.put(info, str)
                    })

                    tableinfo(
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
                        map.get("modified_time")
                    )
                }).toDF()

                .withColumn("order_detail_id", col("order_detail_id").cast(DataTypes.IntegerType))
                .withColumn("product_id", col("product_id").cast(DataTypes.IntegerType))
                .withColumn("product_cnt", col("product_cnt").cast(DataTypes.IntegerType))
                .withColumn("w_id", col("w_id").cast(DataTypes.IntegerType))

                .withColumn("product_price", col("product_price").cast(DataTypes.createDecimalType(8, 2)))
                .withColumn("average_cost", col("average_cost").cast(DataTypes.createDecimalType(8, 2)))
                .withColumn("weight", col("weight").cast(DataTypes.createDecimalType(8, 2)))
                .withColumn("fee_money", col("fee_money").cast(DataTypes.createDecimalType(8, 2)))
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
                .saveAsTable("dwd.fact_order_detail")

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
