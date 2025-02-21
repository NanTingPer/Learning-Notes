package _20250218

import org.apache.hadoop.hbase
import org.apache.hadoop.hbase.CellUtil.cloneFamily
import org.apache.hadoop.hbase.{CellUtil, CompareOperator, HBaseConfiguration}
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.filter.{RegexStringComparator, RowFilter}
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.{col, current_timestamp, date_format, date_trunc, lit, max, round, row_number}
import org.apache.spark.sql.types.DataTypes

import java.util

object 处理04 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession
            .builder()
            .enableHiveSupport()
            .master("local[*]")
            .config("hive.exec.dynamic.partition.mode","nonstrict")
//            .config("spark.sql.WriteOverwrite")
            .appName("dawf")
            .getOrCreate()

        val hiveMaxTime = spark.sql("select max(etl_date) from ods.order_master")
        val hiveData = spark.sql(s"select * from ods.order_master").where(col("etl_date") === hiveMaxTime.first()(0)).drop("etl_date")
        val cols = hiveData.columns.map(col)

        val config = HBaseConfiguration.create()
        config.set(TableInputFormat.INPUT_TABLE,"ods:order_master_offline")
        config.set(TableInputFormat.SCAN, TableMapReduceUtil.convertScanToString(getScan))


        val win1 = Window.partitionBy("order_id").orderBy(col("modified_time").desc)
        val win2 = Window.partitionBy("order_id")



        import spark.implicits._
        spark
        .sparkContext.newAPIHadoopRDD(config,
            classOf[TableInputFormat],
            classOf[ImmutableBytesWritable],
            classOf[Result]).map(f =>
        {
            val map = new util.HashMap[String,String]()
            f._2.rawCells().foreach(cell =>
            {
                //TODO 一个行键、列族、列修饰符、数据和时间戳组合起来叫做一个单元格（Cell）
                //TODO 一条完整的数据,包含多个cell 一个cell相当于 关系型数据库的一行
//                CellUtil.cloneQualifier() //key
                var value = ""
                var info = Bytes.toString(CellUtil.cloneFamily(cell))  //列族
                if(info == "order_id" || info == "customer_id" || info == "order_source" || info == "payment_method" || info == "order_point")
                    value = Bytes.toInt(CellUtil.cloneValue(cell)).toString
                else if (info == "order_money" || info == "district_money" || info == "shipping_money" || info == "payment_money")
                    value = Bytes.toDouble(CellUtil.cloneValue(cell)).toString
                else
                    value = Bytes.toString(CellUtil.cloneValue(cell))
                map.put(info, value)
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
                map.get("modified_time"))
        })
            .toDF()
            .withColumn("order_id",col("order_id").cast(DataTypes.IntegerType))
            .withColumn("customer_id",col("customer_id").cast(DataTypes.IntegerType))
            .withColumn("order_source",col("order_source").cast(DataTypes.IntegerType))
            .withColumn("payment_method",col("payment_method").cast(DataTypes.IntegerType))
            .withColumn("order_point",col("order_point").cast(DataTypes.IntegerType))

//            .withColumn("order_money",col("order_money").cast(DataTypes.DoubleType).cast(DataTypes.createDecimalType()))
            .withColumn("order_money",round(col("order_money").cast(DataTypes.DoubleType), 2))
            .withColumn("district_money",round(col("district_money").cast(DataTypes.DoubleType), 2))
            .withColumn("shipping_money",round(col("shipping_money").cast(DataTypes.DoubleType), 2))
            .withColumn("payment_money",round(col("payment_money").cast(DataTypes.DoubleType), 2))
            //yyyy-MM-dd HH:mm:ss
//            .withColumn("dwd_insert_user",lit("user1"))
//            .withColumn("dwd_insert_time",date_trunc("yyyy-MM-dd HH:mm:ss",current_timestamp()))
//            .withColumn("dwd_modify_user",lit("user1"))
//            .withColumn("dwd_modify_time",date_trunc("yyyy-MM-dd HH:mm:ss",current_timestamp()))

            .select(cols:_*)
            .union(hiveData)
            .withColumn("newCol", row_number().over(win1))
            .where(col("newCol")===1)
            .drop("newCol")
            .withColumn("dwd_insert_user",lit("user1"))
            .withColumn("dwd_insert_time",date_format(current_timestamp(),"yyyy-MM-dd HH:mm:ss"))
            .withColumn("dwd_modify_user",lit("user1"))
            .withColumn("dwd_modify_time",date_format(current_timestamp(),"yyyy-MM-dd HH:mm:ss"))
            .withColumn("etl_date", lit(hiveMaxTime.first()(0)))
            .write
            .format("hive")
            .mode(SaveMode.Overwrite)
            .partitionBy("etl_date")
            .saveAsTable("dwd.fact_order_master")

    }

    def getScan: Scan = {
        val scan = new Scan
        val filter = new RowFilter(CompareOperator.EQUAL, new RegexStringComparator("20221001"))
        scan.setFilter(filter)

        scan
    }
    case class tableInfo(
                        order_id :String,
                        order_sn :String,
                        customer_id :String,
                        shipping_user :String,
                        province :String,
                        city :String,
                        address :String,
                        order_source :String,
                        payment_method :String,
                        order_money :String,
                        district_money :String,
                        shipping_money :String,
                        payment_money :String,
                        shipping_comp_name :String,
                        shipping_sn :String,
                        create_time :String,
                        shipping_time :String,
                        pay_time :String,
                        receive_time :String,
                        order_status :String,
                        order_point :String,
                        invoice_title :String,
                        modified_time :String)


    /**
    抽取ods 库中表order_master 最新分区的数据，
    并结合HBase 中order_master_offline 表中的数据
    合并抽取到dwd 库中fact_order_master 的分区表，
    分区字段为etl_date 且值与ods 库的相对应表该值相等，

    并添加
        dwd_insert_user、
        dwd_insert_time、
        dwd_modify_user、
        dwd_modify_time 四列，
    其中
        dwd_insert_user 、
        dwd_modify_user 均填写“ user1 ” ，

        dwd_insert_time、
        dwd_modify_time 均填写当前操作时间（年月日必须是今 天，时分秒只需在比赛时间范围内即可），

    抽取HBase 中的数据时，
    只抽取2022 年10 月01 日的数据（以rowkey 为准），
    并进行数据类型转换。
    使用 hive cli 查询modified_time 为2022 年10 月01 日当天的数据，
    查询字段为order_id、order_sn、shipping_user、create_time、shipping_time，
    并按照order_id 进行升序排序，
    将结果截图复制粘贴至客户端桌面【Release\模块D 提交结果.docx】中对应的任务序号下；
     */
    case class 题目()
}
























