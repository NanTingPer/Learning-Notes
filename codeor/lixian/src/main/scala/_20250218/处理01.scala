package _20250218

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

object 处理01 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession
                .builder()
                .appName("chouqu01")
                .master("local[*]")
                .config("spark.sql.partitionOverwriteMode","dynamic")
                .config("hive.exec.dynamic.partition.mode","nonstrict")
                .enableHiveSupport()
                .getOrCreate()
        //TODO dim_customer_inf是老数据
        //TODO 这边要做的就把customer_inf的新数据合并到dim_customer_inf
        val oldTableMaxTime = spark.sql("select max(etl_date) from dwd.dim_customer_inf").first()(0)
        var oldTableData = spark.sql(s"select * from dwd.dim_customer_inf where etl_date='${oldTableMaxTime}'")
        val oldTableCol = oldTableData.columns.map(f => col(f))


        //TODO 因为新数据没有这四列，而且列顺序可能不一样 所以使用select查询列
        val newTableMaxTime = spark.sql("select max(etl_date) from ods.customer_inf").first()(0)
        val newTableData = spark
                .sql(s"select * from ods.customer_inf where etl_date='${newTableMaxTime}'")
                .withColumn("dwd_insert_user", lit("user1"))
                .withColumn("dwd_insert_time", date_trunc("yyyy-MM-dd HH:mm:ss",current_timestamp()))
                .withColumn("dwd_modify_user", lit("user1"))
                .withColumn("dwd_modify_time", date_trunc("yyyy-MM-dd HH:mm:ss",current_timestamp()))
                .select(oldTableCol:_*)

        oldTableData = oldTableData
                .drop("etl_date")
                .withColumn("etl_date",lit(newTableMaxTime))

        val untable = newTableData.union(oldTableData)

        val win1 = Window.partitionBy("customer_id").orderBy(col("modified_time").desc)
        val win2 = Window.partitionBy("customer_id")

        untable
                .withColumn("tempCol", row_number().over(win1))
                .withColumn("dwd_insert_time", min(col("dwd_insert_time")).over(win2))
                .withColumn("dwd_modify_time", max(col("dwd_modify_time")).over(win2))
                .createTempView("untable")

        spark
                .sql("select * from untable where tempCol = 1")
                .drop(col("tempCol"))
                .write
                .mode(SaveMode.Overwrite)
                .format("hive")
                .insertInto("dwd.dim_customer_inf")


    }

    /**
    create table if not exists dwd.dim_customer_inf(
    customer_inf_id bigint,
    customer_id bigint,
    customer_name string,
    identity_card_type int,
    identity_card_no string,
    mobile_phone string,
    customer_email string,
    gender string,
    customer_point bigint,
    register_time timestamp,
    birthday timestamp,
    customer_level int,
    customer_money decimal(8,2),
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

    insert into dwd.dim_customer_inf values(0,-1,"鞠桂枝",1,611325198211210472,13572811239,"songjuan@mail.com","",10564,"2032-08-16 08:48:36","1904-07-15 00:00:00",2,23675.00,"2012-08-22 03:45:36","user1","2023-12-23 09:53:08","user1","2023-12-23 09:53:08","20231225");

    insert into dwd.dim_customer_inf values(0,0,"鞠桂枝",1,611325198211210472,13572811239,"songjuan@mail.com","",10564,"2032-08-16 08:48:36","1904-07-15 00:00:00",2,23675.00,"2012-08-22 03:45:36","user1","2023-12-23 09:53:08","user1","2023-12-23 09:53:08","20231225");
     */
    case  class tem()

}
