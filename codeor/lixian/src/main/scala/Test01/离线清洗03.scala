package Test01

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

import java.time.LocalTime
import scala.Predef.genericArrayOps

object 离线清洗03 {
    //抽取ods 库中表product_info 最新分区的数据，
    // 并结合dim_product_info最新分区现有的数据，
    // 根据product_core 合并数据到dwd 库中 dim_product_info 的分区表

    // （合并是指对dwd 层数据进行插入或修改，需修改的数据以product_core 为合并字段，根据modified_time 排序取最新的 一条），

    // 分区字段为etl_date 且值与ods 库的相对应表该值相等，
    // 并添加dwd_insert_user、dwd_insert_time、dwd_modify_user、dwd_modify_time 四列，
    // 其中dwd_insert_user、dwd_modify_user 均填写“user1”。
    // 若该条记录第一次进入数仓dwd 层则dwd_insert_time、dwd_modify_time 均存当 前操作时间，
    // 并进行数据类型转换。若该数据在进入dwd 层时发生了合并修改，
    // 则dwd_insert_time 时间不变，dwd_modify_time 存当前操作时间，其 余列存最新的值。

    // 使用hive cli 执行show partitionsdwd.dim_product_info 命令，将结果截图粘贴至客户端桌面【Release\模块D 提交结果.docx】中对应的任务序号下；
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession.builder()
                .master("local[*]")
                .appName("spark03")
                .config("hive.exec.scratchdir","hdfs://192.168.45.13:9000/user/hive/temp")
                .config("hive.exec.dynamic.partition.mode","nonstrict")
                .enableHiveSupport()
                .getOrCreate()

        var maxtime = spark.sql("select max(etl_date) from dwd.dim_product_info").first()(0)
        val dt = spark.sql(s"select * from dwd.dim_product_info where etl_date = ${maxtime}")
        val new_col = dt.columns.map(col(_))

        maxtime = spark.sql("select max(etl_date) from ods.product_info").first()(0)
        val newtablecol = spark.sql(s"select * from ods.product_info where etl_date = '${maxtime}'")
                .withColumn("dwd_insert_user", lit("user1"))
                .withColumn("dwd_insert_time", date_trunc("second", current_timestamp()))
                .withColumn("dwd_modify_user", lit("user1"))
                .withColumn("dwd_modify_time", date_trunc("second", current_timestamp()))
                .select(new_col: _*)

        val win1 = Window.partitionBy("product_core").orderBy(col("modified_time").desc)
        val win2 = Window.partitionBy("product_core")

        val nutable = dt.union(newtablecol)

        nutable.withColumn("num",row_number().over(win1))
                .withColumn("dwd_insert_time",min("dwd_insert_time").over(win2))
                .withColumn("dwd_modify_time",max("dwd_modify_time").over(win2))
                .createTempView("temp")
        spark.sql("select * from temp where num = 1").drop(col("num"))
                .write
                .format("hive")
                .mode(SaveMode.Overwrite)
                .insertInto("dwd.dim_product_info")

    }
}
