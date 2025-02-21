package _20250218

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

object 处理03 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession
                .builder()
                .enableHiveSupport()
                .config("hive.exec.dynamic.partition.mode","nonstrict")
                .config("spark.sql.PartitionOverwriteMode","dynamic")
                .master("local[*]")
                .appName("dwaf")
                .getOrCreate()

        val oldTime = spark.sql("select max(etl_date) from dwd.dim_product_info")
        val newTime = spark.sql("select max(etl_date) from ods.product_info")
        val oldData = spark.sql(s"select * from dwd.dim_product_info where etl_date='${oldTime}'").drop("etl_date").withColumn("etl_date",lit(oldTime.first()(0)))


        val cols = oldData.columns.map(col)
        val formate = "yyyy-MM-dd HH:mm:ss"
        val untable = spark
                .sql(s"select * from ods.product_info where etl_date='${newTime}'")
                .withColumn("dwd_insert_user", lit("user1"))
                .withColumn("dwd_insert_time", date_trunc(formate, current_timestamp()))
                .withColumn("dwd_modify_user", lit("user1"))
                .withColumn("dwd_modify_time", date_trunc(formate, current_timestamp()))
                .select(cols:_*)
                .union(oldData)

        val win1 = Window.partitionBy("product_core").orderBy(col("modified_time").desc)
        val win2 = Window.partitionBy("product_core")

        untable
                .withColumn("tempcol", row_number().over(win1))
                .withColumn("dwd_insert_time", min("dwd_insert_time").over(win2))
                .withColumn("dwd_modify_time", max("dwd_modify_time").over(win2))
                .where(col("tempcol")===1)
                .drop("tempcol")
                .write
                .mode(SaveMode.Overwrite)
                .format("hive")
                .insertInto("dwd.dim_product_info")
    }

    /**
    抽取ods 库中表product_info 最新分区的数据，
    并结合dim_product_info最新分区现有的数据，
    根据product_core 合并数据到dwd 库中 dim_product_info 的分区表
        （合并是指对dwd 层数据进行插入或修改，需修改的数据以product_core 为合并字段，根据modified_time 排序取最新的 一条），
    分区字段为etl_date 且值与ods 库的相对应表该值相等，

    并添加
        dwd_insert_user、
        dwd_insert_time、
        dwd_modify_user、
        dwd_modify_time 四列，

    其中
        dwd_insert_user、
        dwd_modify_user 均填写“user1”。

    若该条记录第一次进入数仓dwd 层
        则dwd_insert_time、dwd_modify_time 均存当 前操作时间，

    并进行数据类型转换。
    若该数据在进入dwd 层时发生了合并修改，
        则dwd_insert_time 时间不变，dwd_modify_time 存当前操作时间，

    其 余列存最新的值。使用hive cli 执行show partitionsdwd.dim_product_info 命令，
    将结果截图粘贴至客户端桌面【Release\模块D 提交结果.docx】中对应的任务序号下；
     */
    case class 题目()


    /**
    hive> set hive.exec.dynamic.partition.mode=nonstrict;       // 分区模式，默认strict
    hive> set hive.exec.dynamic.partition=true;                       // 开启动态分区,默认false
    hive> set hive.exec.max.dynamic.partitions=1000;	           //最大动态分区数, 设为1000

    //TODO if not find database => create database dwd
    create table if not exists dwd.dim_product_info(
        product_id bigint,
        product_core string,
        product_name string,
        bar_code string,
        brand_id bigint,
        one_category_id int,
        two_category_id int,
        three_category_id int,
        supplier_id bigint,
        price decimal(8,2),
        average_cost decimal(18,2),
        publish_status int,
        audit_status int,
        weight float,
        length float,
        height float,
        width float,
        color_type string,
        production_date timestamp,
        shelf_life bigint,
        descript string,
        indate timestamp,
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

    //TODO 这是一条测试语句，不会被修改 is ??????? => The Test Code , Not Modify
    //TODO 这是一条测试语句，但是会被修改 is ??????? => The Test Code , Yes Modify
    //TODO 描述信息 is ????? => Tooltip_Info
    insert into dwd.dim_product_info values(0,"1112223334444","The Test Code , Not Modify","1080918117235",7,1,5,8,792,983.47,123.82,0,1,6.11,5.35,2.06,5.21,"yellow","2022-01-16 00:51:05",36,"Tooltip_Info","2022-08-28 05:13:45","2002-09-04 22:56:45","user1","2023-12-23 09:53:08","user1","2023-12-23 09:53:08","20231225");
    insert into dwd.dim_product_info values(0,"1571120092163","The Test Code , Yes Modify","1080918117235",7,1,5,8,792,983.47,123.82,0,1,6.11,5.35,2.06,5.21,"yellow","2022-01-16 00:51:05",36,"Tooltip_Info","2022-08-28 05:13:45","2002-09-04 22:56:45","user1","2023-12-23 09:53:08","user1","2023-12-23 09:53:08","20231225");
     */
    case class 前倾概要()
}
