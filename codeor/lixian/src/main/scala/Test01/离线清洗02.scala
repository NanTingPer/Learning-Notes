package Test01

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

import java.util.Properties

object 离线清洗02 {
    def main(args: Array[String]): Unit = {
        //        2、抽取ods 库中表coupon_info 最新分区数据，并
        //        结合dim_coupon_info 最新分区现有的数据，
        //        根据coupon_id 合并数据到dwd 库
        //        中dim_coupon_info 的分区表
        //        （合并是指对dwd 层数据进行插入或修改，需修改的数据以coupon_id为合并字段，根据modified_time 排序取最新的一条），
        //        分区字段为etl_date且值与ods 库的相对应表该值相等，
        //        并添加dwd_insert_user、
        //              dwd_insert_time、
        //              dwd_modify_user、
        //              dwd_modify_time 四列，
        //        其中dwd_insert_user、dwd_modify_user 均填写“user1”。
        //        若该条记录第一次进入数仓dwd 层则dwd_insert_time、dwd_modify_time 均存当前操作时间，
        //        并进行数据类型转换。若该数据在进入dwd 层时发生了合并修改，
        //        则dwd_insert_time 时间不变，dwd_modify_time 存当前操作时间，
        //        其余列存 最新的值。使用hive cli 执行show partitions dwd.dim_coupon_info 命令，
        //        将结果截图粘贴至客户端桌面【Release\模块D 提交结果.docx】中对应的任务序号下；
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession.builder()
                .master("local[*]")
                .appName("spark2")
                .config("hive.exec.scratchdir","hdfs://192.168.45.13:9000/user/hive/temp")
                .config("hive.exec.dynamic.partition.mode","nonstrict")
                .enableHiveSupport()
                .getOrCreate()
        val MTable = "coupon_info"
        val MData = "ds_db01"
        val HTable = "dim_coupon_info"
        val HData ="dwd"
        val HTD = "dwd.dim_coupon_info"
        //ods.coupon_info

        spark.sql("select * from dwd.dim_coupon_info").createTempView("old")
        var maxTime = spark.sql("select max(etl_date) from old").first()(0)
        val mySQLTable = spark.sql(s"select * from old where etl_date = '${maxTime}'")
        var allCol = mySQLTable.columns.map(col(_))



        maxTime = spark.sql("select max(etl_date) from ods.coupon_info")
        //        dwd_insert_user
        //        dwd_insert_time
        //        dwd_modify_user
        //        dwd_modify_time
        val hiveTable = spark.sql(s"select * from ${HTD} where etl_date = '${maxTime}'")
                .withColumn("dwd_insert_user", lit("user1"))
                .withColumn("dwd_insert_time", date_trunc("second", current_timestamp()))
                .withColumn("dwd_modify_user", lit("user1"))
                .withColumn("dwd_modify_time", date_trunc("second", current_timestamp()))
                .select(allCol: _*)

        val unTable = mySQLTable.union(hiveTable)

        val win1 = Window.partitionBy("coupon_id").orderBy(col("modified_time").desc)
        val win2 = Window.partitionBy("coupon_id")

        unTable.withColumn("num",row_number().over(win1))
                .withColumn("dwd_insert_time",min(col("dwd_insert_time")).over(win2))
                .withColumn("dwd_modify_time",max(col("dwd_insert_time")).over(win2))
                .createTempView("ut")

        spark.sql("select * from ut where num = 1")
                .drop(col("num"))
                .write
                .mode(SaveMode.Overwrite)
                .format("hive")
                .insertInto(HTD)


    }
}
