package _20250218

import org.apache.hadoop.hbase.{CellUtil, CompareOperator, HBaseConfiguration, client, io}
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions.{col, current_timestamp, date_format, lit, round, row_number}
import org.apache.hadoop.hbase.io._
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.filter.{RegexStringComparator, RowFilter}
import org.apache.hadoop.hbase.mapreduce.{TableInputFormat, TableMapReduceUtil}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.types.DataTypes

import java.util
//TODO create 'ods:order_detail_offline','info'
//TODO
object 处理05{
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")

        val spark = SparkSession
            .builder()
            .master("local[*]")
            .appName("dawf")
            .enableHiveSupport()
            .getOrCreate()

        //TODO 获取最大时间和表全部行，用于Hbase表对齐
        val maxtime = spark.sql("select max(etl_date) from ods.order_detail").first()(0)
        val hiveData = spark.sql(s"select * from ods.order_detail where etl_date='${maxtime}'").drop("etl_date")
        val cols = hiveData.columns.map(col)


        val config = HBaseConfiguration.create()
        config.set(TableInputFormat.INPUT_TABLE,"ods:order_detail_offline")
        config.set(TableInputFormat.SCAN, TableMapReduceUtil.convertScanToString(new Scan().setFilter(new RowFilter(CompareOperator.EQUAL, new RegexStringComparator("20221001")))))

        //TODO 创建一个窗口 用于截取新的一条数据
        val win = Window.partitionBy("order_detail_id").orderBy(lit("modified_time").desc)
        import spark.implicits._
        spark.sparkContext.newAPIHadoopRDD(config,
            classOf[TableInputFormat],
            classOf[ImmutableBytesWritable],
            classOf[Result])
        .map(f =>
        {
            val map = new util.HashMap[String,String]()
            f._2.rawCells().foreach(f =>
            {
                val info = Bytes.toString(CellUtil.cloneQualifier(f))

                //TODO 将二进制数据 按照表字段类型 进行相应的转换
                val value = info match
                {
                    case
                        "order_detail_id"
                    ||  "product_id"
                    ||  "product_cnt"
                    ||  "w_id"
                    => Bytes.toLong(CellUtil.cloneValue(f)).toString
                    case
                        "product_name"
                    ||  "product_price"
                    ||  "average_cost"
                    ||  "weight"
                    ||  "fee_money"
                    => Bytes.toDouble(CellUtil.cloneValue(f)).toString
                    case _ => Bytes.toString(CellUtil.cloneValue(f))
                }
                map.put(info, value)

            })
            tableInfo(
                map.get("order_detail_id"),
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
        //TODO Hbase处理时，全部转为了String 这里进行数据类型转换
        .withColumn("product_name",round(lit("product_name").cast(DataTypes.DoubleType),2))
        .withColumn("product_price",round(lit("product_price").cast(DataTypes.DoubleType),2))
        .withColumn("average_cost",round(lit("average_cost").cast(DataTypes.DoubleType),2))
        .withColumn("weight",round(lit("weight").cast(DataTypes.DoubleType),2))
        .withColumn("fee_money",round(lit("fee_money").cast(DataTypes.DoubleType),2))
        .withColumn("order_detail_id",lit("order_detail_id").cast(DataTypes.IntegerType))
        .withColumn("product_id",lit("product_id").cast(DataTypes.IntegerType))
        .withColumn("product_cnt",lit("product_cnt").cast(DataTypes.IntegerType))
        .withColumn("w_id",lit("w_id").cast(DataTypes.IntegerType))
        //TODO 字段对齐 不然un会报错
        .select(cols:_*)
        .union(hiveData)
        //TODO 只截取最新的一条
        .withColumn("coltemp",row_number().over(win))
        .where(col("coltemp")===1)
        .drop("coltemp")
        .withColumn("dwd_insert_user", lit("user1"))
        .withColumn("dwd_insert_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss"))
        .withColumn("dwd_modify_user", lit("user1"))
        .withColumn("dwd_modify_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss"))
        .withColumn("etl_date", lit(maxtime))
        .write
        .mode(SaveMode.Overwrite)
        .format("hive")
        .partitionBy("etl_date")
        .saveAsTable("dwd.fact_order_detail")
    }

    case class tableInfo(
        order_detail_id : String,
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
}


