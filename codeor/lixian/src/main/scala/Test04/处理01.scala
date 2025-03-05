package Test04

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.api.java.UDF2
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.DataTypes

object 处理01 {

//    抽取ods库中user_info表中昨天的分区（子任务一生成的分区） 数据，
//    并结合dim_user_info最新分区现有的数据，
//    根据id合并  数据到dwd库中dim_user_info的分区表（合并是指对dwd层数据进行插入或修改，
//    需修改的数据以id为合并字段，根据operate_time排序取最新的一条），
//    分区字段为etl_date且值与ods库的相对应表该值相等，同时若operate_time为空，则用creat e_time填充，
//    并添加dwd_insert_user、dwd_insert_time、dwd_modify_user、dwd_modify_time四列,
//    其中dwd_insert_user、d wd_modify_user 均填写“user1”。
//    若该条记录第一次进入数仓 d wd 层则 dwd_insert_time、dwd_modify_time 均存当前操作时间，
//    并进行数据类型转换。若该数据在进入dwd层时发生了合并修改，
//    则 dwd_insert_time 时间不变，dwd_modify_time 存当前操作时 间，
//    其余列存最新的值。
//    使用hive cli执行show partitions dwd.dim_user_info命令，将结果截图粘贴至客户端桌面【Release\任务B提交结果.docx】中对应的任务序号下
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession
            .builder()
            .master("local[*]")
            .appName("hive")
            .enableHiveSupport()
            .getOrCreate()
        val odsdate = "20250304"
        val odsdata = spark.sql(s"select * from ods.user_info where etl_date = '20250304'")
            .withColumn("dwd_insert_user", lit("user1"))
            .withColumn("dwd_insert_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss"))//yyyy-MM-dd HH:mm:ss
            .withColumn("dwd_modify_user", lit("user1"))
            .withColumn("dwd_modify_time", date_format(current_timestamp(), "yyyy-MM-dd HH:mm:ss"))

        val dwdMaxData = spark.sql("select max(etl_date) from dwd.dim_user_info")
        val dwddata = spark.sql(s"select * from dwd.dim_user_info where etl_date = '${dwdMaxData}'")
        val allCol = dwddata.columns.map(col)
        val hunhe = odsdata.select(allCol:_*)

        val win1 = Window.partitionBy("id").orderBy(col("operate_time").desc)
        val win2 = Window.partitionBy("id")

        spark.udf.register("test", new UDF2[String, String, String] {
            override def call(t1: String, t2: String): String = {
                if(t1 == null) t2
                else t1
            }
        }, DataTypes.StringType)

        hunhe.union(dwddata)
            .withColumn("temp", row_number().over(win1))
            .withColumn("dwd_modify_time", max(col("dwd_modify_time")).over(win2))
            .withColumn("dwd_insert_time", min(col("dwd_modify_time")).over(win2))
            .where(col("temp") === 1)
            .drop("temp").createTempView("data")
//            .withColumn("operate_time", coalesce())
        spark.sql("select *, test(operate_time, create_time) as newoperate_time from data")
            .withColumn("operate_time", col("newoperate_time"))
            .drop("newoperate_time").show()

    }
}
