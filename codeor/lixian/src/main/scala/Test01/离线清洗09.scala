package Test01

import com.google.protobuf.CodedInputStream
import org.apache.hadoop.hbase.{CellUtil, CompareOperator, HBaseConfiguration}
import org.apache.hadoop.hbase.client.{Result, Scan}
import org.apache.hadoop.hbase.filter.{RegexStringComparator, RowFilter}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions.{col, current_timestamp, date_trunc, lit}
import org.apache.spark.sql.types.{DataTypes, DateType}

import java.time.LocalTime
import java.util

object 离线清洗09 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val sq = SparkSession.builder()
                .master("local[*]")
                .appName("spark09")
                .enableHiveSupport()
                .config("hive.exec.scratchdir","hdfs://192.168.45.13:9000/use/hive/temp")
                .config("hive.exec.dynamic.partition.mode","nonstrict")
                .getOrCreate()

        val mt = sq.sql("select max(etl_date) from ods.product_browse").first()(0)
        val odsdata = sq.sql(s"select * from ods.product_browse where etl_date = '${mt}'")

        val conf = HBaseConfiguration.create()
        conf.set(TableInputFormat.INPUT_TABLE, "dwd:product_browse_offline")
        conf.set(TableInputFormat.SCAN, TableMapReduceUtil.convertScanToString(getscan))


        import sq.implicits._
        sq.sparkContext.newAPIHadoopRDD(
            conf,
            classOf[TableInputFormat],
            classOf[ImmutableBytesWritable],
            classOf[Result]
        ).map(f => {
            val cells = f._2
            val map = new util.HashMap[String,String]()

            cells.rawCells().foreach(f =>{
                val info = Bytes.toString(CellUtil.cloneQualifier(f))
                var value = ""
                if(info == "log_id" || info == "product_id" || info == "gen_order"){
                    value = Bytes.toLong(CellUtil.cloneValue(f)).toString
                }else if(info == "modified_time"){
                    value = Bytes.toDouble(CellUtil.cloneValue(f)).toString
                }else{
                    value = Bytes.toString(CellUtil.cloneValue(f))
                }
                map.put(info,value)
            })

            tableinfo(map.get("log_id"),
                    map.get("product_id"),
                    map.get("customer_id"),
                    map.get("gen_order"),
                    map.get("order_sn"),
                    map.get("modified_time"))

        })
                .toDF()
                .withColumn("log_id",col("log_id").cast(DataTypes.IntegerType))
                .withColumn("product_id",col("product_id").cast(DataTypes.IntegerType))
                .withColumn("order_sn",col("order_sn").cast(DataTypes.IntegerType))
                .withColumn("etl_date",lit(mt))

                .union(odsdata)

                .withColumn("modified_time",col("modified_time").cast(DataTypes.createDecimalType(8,2)))
                .withColumn("dwd_insert_user",lit("user1"))
                .withColumn("dwd_insert_modify",date_trunc("second",current_timestamp()))
                .withColumn("dwd_modify_user",lit("user1"))
                .withColumn("dwd_modify_time",date_trunc("second",current_timestamp()))

                .write
                .mode(SaveMode.Overwrite)
                .format("hive")
                .partitionBy("etl_date")
                .saveAsTable("dwd.log_product_browse")


    }

    def getscan ={
        val scan = new Scan

        val filter = new RowFilter(CompareOperator.EQUAL, new RegexStringComparator("20221001"))
        scan.setFilter(filter)
        scan.withStartRow(Bytes.toBytes("0+1001000000000"))
        scan.withStopRow(Bytes.toBytes("0+1001245959999"))
        scan
    }

    case class tableinfo(
                        log_id : String,
                        product_id :String,
                        customer_id : String,
                        gen_order : String,
                        order_sn : String,
                        modified_time : String
                        )

}
