package Test03

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.{CellUtil, CompareOperator}
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.filter.{RegexStringComparator, RowFilter}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce._
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions.{col, current_timestamp, date_trunc, lit}
import org.apache.spark.sql.types.DataTypes

import java.util

object qinxi05 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")

        val sp = SparkSession.builder()
                .master("local[*]")
                .appName("wf")
                .config("hive.exec.scratchdir","hdfs://192.168.45.13:9000/user/temp/hive")
                .config("hive.exec.dynamic.partition.mode","nonstrict")
                .enableHiveSupport()
                .getOrCreate()

        val mtime = sp.sql("select max(etl_date) from ods.order_detail").first()(0)
        val newdata = sp.sql(s"select * from ods.order_detail where etl_date = '${mtime}'")

        val con = new Configuration()
        con.set(TableInputFormat.INPUT_TABLE,"ods:order_detail_offline")
        con.set(TableInputFormat.SCAN,TableMapReduceUtil.convertScanToString(getScan))
        con.set("hbase.zookeeper.quorum","192.168.45.13:2181")

        import sp.implicits._

        sp.sparkContext.newAPIHadoopRDD(
            con,
            classOf[TableInputFormat],
            classOf[ImmutableBytesWritable],
            classOf[Result]
        ).map(f => {
            val map = new util.HashMap[String,String]()
            f._2.rawCells().foreach(f => {
                val info = Bytes.toString(CellUtil.cloneQualifier(f))
                val value = CellUtil.cloneValue(f)
                var fvalue = ""
                if(info == "order_detail_id" || info == "product_id" || info == "product_cnt" || info == "w_id"){
                    fvalue = Bytes.toLong(value).toString
                }else if(info == "product_price" || info == "average_cost" || info == "weight" || info == "fee_money"){
                    fvalue = Bytes.toDouble(value).toString
                }else{
                    fvalue = Bytes.toString(value)
                }
                map.put(info,fvalue)
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
        })
                .toDF()
                .withColumn("order_detail_id",col("order_detail_id").cast(DataTypes.IntegerType))
                .withColumn("product_id",col("product_id").cast(DataTypes.IntegerType))
                .withColumn("product_cnt",col("product_cnt").cast(DataTypes.IntegerType))
                .withColumn("w_id",col("w_id").cast(DataTypes.IntegerType))

                .withColumn("product_price",col("product_price").cast(DataTypes.DoubleType))
                .withColumn("average_cost",col("average_cost").cast(DataTypes.DoubleType))
                .withColumn("weight",col("weight").cast(DataTypes.DoubleType))
                .withColumn("fee_money",col("fee_money").cast(DataTypes.DoubleType))
                .withColumn("etl_date",lit(s"${mtime}"))

                .union(newdata)

                .withColumn("dwd_insert_user",lit("user1"))
                .withColumn("dwd_insert_time",date_trunc("second",current_timestamp()))
                .withColumn("dwd_modify_user",lit("user1"))
                .withColumn("dwd_modify_time",date_trunc("second",current_timestamp()))

                .write
                .mode(SaveMode.Overwrite)
                .partitionBy("etl_date")
                .format("hive")
                .saveAsTable("dwd.fact_order_detail")
    }



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
                        modified_time : String
                        )

    def getScan ={
        val scan = new Scan()

        val filter = new RowFilter(CompareOperator.EQUAL,new RegexStringComparator("20221001"))
        scan.withStartRow(Bytes.toBytes("0+20221001000000000"))
        scan.withStopRow(Bytes.toBytes("0+20221001245959999"))
        scan.setFilter(filter)

        scan
    }
}
