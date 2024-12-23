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

object qinxi9 {

    case class tableinfo(
                        log_id : String,
                        product_id : String,
                        customer_id : String,
                        gen_order : String,
                        order_sn : String,
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

        val maxtime = sp.sql("select max(etl_date) from ods.product_browse").first()(0)
        val hivetable = sp.sql(s"select * from ods.product_browse where etl_date = '${maxtime}'")


        import sp.implicits._
        val config = new Configuration();
        config.set(TableInputFormat.INPUT_TABLE,"ods:product_browse")
        config.set(TableInputFormat.SCAN,TableMapReduceUtil.convertScanToString(getscan))
        config.set("hbase.zookeeper.quorum","192.168.45.20:2181")

        val todf = sp.sparkContext.newAPIHadoopRDD(
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
                        if (info == "log_id" || info == "product_id" || info == "gen_order") {
                            str = Bytes.toLong(byvalue).toString
                        } else {
                            str = Bytes.toString(byvalue)
                        }
                        map.put(info, str)
                    })

                    tableinfo(
                        map.get("log_id"),
                        map.get("product_id"),
                        map.get("customer_id"),
                        map.get("gen_order"),
                        map.get("order_sn"),
                        map.get("modified_time")
                    )
                }).toDF()

        todf.show

        todf

                .withColumn("log_id", col("log_id").cast(DataTypes.IntegerType))
                .withColumn("product_id", col("product_id").cast(DataTypes.IntegerType))
                .withColumn("gen_order", col("gen_order").cast(DataTypes.IntegerType))

                .withColumn("modified_time", col("modified_time").cast(DataTypes.createDecimalType(8, 2)))
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
                .saveAsTable("dwd.log_product_browse")

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
