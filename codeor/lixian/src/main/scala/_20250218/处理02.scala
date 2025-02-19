package _20250218

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

object 处理02 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME", "root")
        val spark = SparkSession
                .builder()
                .appName("adf")
                .master("local[*]")
                .config("hive.exec.dynamic.partition.mode","nonstrict")
                .enableHiveSupport()
                .getOrCreate()
        //                .config("spark.sql.partitionOver","dy")

        val oldMaxTime = spark.sql("select max(etl_date) from dwd.dim_coupon_info")
        var oldData = spark.sql(s"select * from dwd.dim_coupon_info where etl_date='${oldMaxTime}'")
        val cols = oldData.columns.map(col)

        val newMaxTime = spark.sql("select max(etl_date) from ods.coupon_info")
        val newData = spark
                .sql(s"select * from ods.coupon_info where etl_date='${newMaxTime}'")
                .withColumn("dwd_insert_user",  lit("user1"))
                .withColumn("dwd_insert_time",  date_trunc("yyyy-MM-dd HH:mm:ss", current_timestamp()))
                .withColumn("dwd_modify_user",  lit("user1"))
                .withColumn("dwd_modify_time",  date_trunc("yyyy-MM-dd HH:mm:ss", current_timestamp()))
                .select(cols:_*)

        oldData = oldData.drop("etl_date").withColumn("etl_date", lit(newMaxTime.first()(0)))

        val untable = newData.union(oldData)

        val win1 = Window.partitionBy("coupon_id").orderBy(col("modified_time").desc)
        val win2 = Window.partitionBy("coupon_id")

        untable
                .withColumn("rownumb",row_number().over(win1))
                .withColumn("dwd_insert_time", min(col("dwd_insert_time")) over win2)
                .withColumn("dwd_modify_time", max(col("dwd_modify_time")) over win2)
                .where(col("rownumb") === 1)
                .drop(col("rownumb"))
                .write
                .mode(SaveMode.Overwrite)
                .format("hive")
                .insertInto("dwd.dim_coupon_info")

    }

    /**
    hive> set hive.exec.dynamic.partition.mode=nonstrict;       // 分区模式，默认strict
    hive> set hive.exec.dynamic.partition=true;                       // 开启动态分区,默认false
    hive> set hive.exec.max.dynamic.partitions=1000;	           //最大动态分区数, 设为1000

    //TODO if not dwd database => create database dwd;

    create table if not exists dwd.dim_coupon_info(
      coupon_id bigint,
      coupon_name string,
      coupon_type int,
      condition_amount bigint,
      condition_num bigint,
      activity_id string,
      benefit_amount decimal(8,2),
      benefit_discount decimal(8,2),
      modified_time timestamp,
      dwd_insert_user string,
      dwd_insert_time timestamp,
      dwd_modify_user string,
      dwd_modify_time timestamp
    )
    partitioned by (etl_date string)
    row format delimited
    fields terminated by ','
    stored as textfile;

    //TODO if "11折优惠券" is "11??????" => 11zyhj
    insert into dwd.dim_coupon_info values(0,"11zyhj",11,11000,3,"6569962031230",0.00,0.10,"2022-09-09 06:23:07","user1","2023-12-23 09:53:08","user1","2023-12-23 09:53:08","20231225");

    insert into dwd.dim_coupon_info values(1,"11zyhj",11,11000,3,"6569962031230",0.00,0.10,"2022-09-09 06:23:07","user1","2023-12-23 09:53:08","user1","2023-12-23 09:53:08","20231225");
     */
    case class tem()

    /**
    抽取ods 库中表coupon_info 最新分区数据，
    结合dim_coupon_info 最新分区现有的数据，
    根据coupon_id 合并数据到dwd 库中dim_coupon_info 的分区表（合并是指对dwd 层数据进行插入或修改，
    需修改的数据以coupon_id为合并字段，
    根据modified_time 排序取最新的一条），
    分区字段为etl_date且值与ods 库的相对应表该值相等，

    并添加
        dwd_insert_user、
        dwd_insert_time、
        dwd_modify_user、
        dwd_modify_time 四列，

    其中
        dwd_insert_user、dwd_modify_user 均填写“user1”。

    若该条记录第一次进入数仓dwd 层
        则dwd_insert_time、dwd_modify_time 均存当前操作时间，
        并进行数据类型转换。

    若该数据在进入dwd 层时发生了合并修改，
        则dwd_insert_time 时间不变，
        dwd_modify_time 存当前操作时间，
        其余列存 最新的值。

    使用hive cli 执行show partitions dwd.dim_coupon_info 命令，
    将结果截图粘贴至客户端桌面【Release\模块D 提交结果.docx】中对应的任务序号下；
     */
    case class awd(a : String)
}
