package Test01

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

import java.sql.Timestamp
import scala.:+

//TODO 离线数据采集 => ods库(新表)
//TODO 新表数据 => 插入dwd库(老表)
//TODO dwd_insert_time之所以取最小，是因为如果un过程发生了更改，当前操作时间永远是最大的

object 离线清洗 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")

        var spark = SparkSession.builder()
                .master("local[*]")
                .enableHiveSupport()
                .appName("test")
                .config("hive.exec.scratchdir","hdfs://192.168.45.13:9000/user/hive/temp")
                .config("hive.exec.dynamic.partition.mode","nonstrict")
                .getOrCreate()

        //TODO 因为是最新数据所以获取老表的最大时间
        var maxtime = spark.sql("select max(etl_date) from dwd.dim_customer_inf").first()(0)
        //TODO 根据最大时间获取分区数据 这个数据就是最新分区数据
        var old_table = spark.sql(s"select * from dwd.dim_customer_inf where etl_date = '${maxtime}'")
        //TODO 获取源表全部列
        val columns = old_table.columns.map(col(_))


        //TODO 获取新表的最大时间
        maxtime = spark.sql("select max(etl_date) from ods.customer_inf").first()(0)

        //TODO 根据customer_id 合并数据到dwd 库中dim_customer_inf 的分区表
        //TODO 由于需要合并到分区表 所以查询条件需要过滤分区数据
        //TODO 过滤条件是最新分区数据
        val new_table = spark.sql(s"select * from ods.customer_inf where etl_date = '${maxtime}'")
                //TODO lit用于创建 col用于引用

                //TODO 并添加 dwd_insert_user、dwd_insert_time、dwd_modify_user、dwd_modify_time四列，
                //题目臭又长 user结尾插入user1 time结尾插入取秒的时间戳
                .withColumn("dwd_insert_user", lit("user1"))
                .withColumn("dwd_insert_time", date_trunc("second", current_timestamp()))
                .withColumn("dwd_modify_user", lit("user1"))
                .withColumn("dwd_modify_time", date_trunc("second", current_timestamp()))
                //TODO 这行估计是因为这个查询的表没有这个字段 所以需要这样查一下
                //TODO 这样之后 new_table的字段就跟 old_table一样的
                .select(columns : _*)

        val un_table = old_table.union(new_table)

        //TODO 创建分组窗口 分组字段为合并字段
        //TODO 对时间进行降序排序 指定的列必须使用col引用，不然没有desc方法
        //TODO 之所以降序排序 是因为需要取modified_time最新的
        val window1 = Window.partitionBy("customer_id").orderBy(col("modified_time").desc)
        val window2 = Window.partitionBy("customer_id")

        //TODO 添加行号 降序排序
        un_table.withColumn("num", row_number.over(window1))
                .withColumn("dwd_insert_time", min("dwd_insert_time").over(window2))
                .withColumn("dwd_modify_time", max("dwd_modify_time").over(window2))
                .createTempView("runtable")

//        spark.sqlContext.sql("select * from runtable where num = 1").drop("num").show

        spark.sqlContext.sql("select * from runtable where num = 1")
                .drop(col("num"))
                .write
                .format("hive")
                .mode(SaveMode.Overwrite)
                .insertInto("dwd.dim_customer_inf")

    }
}
