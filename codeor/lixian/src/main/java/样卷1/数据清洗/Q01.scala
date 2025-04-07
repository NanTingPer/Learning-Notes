package 样卷1.数据清洗

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions._

object Q01 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")

        val spark = SparkSession
            .builder()
            .appName("hive")
            .enableHiveSupport()
            .getOrCreate()

        //todo 从ods库中读取user_info最新分区数据(昨天)
        //todo 可以使用硬编码，也可以像我这样使用函数计算
        val odsTable = spark.sql("select * from ods.user_info")
        val odsMaxPartitionValue = odsTable.select(max("etl_date")).first()(0)
        val odsNewPartitionData = odsTable.where(col("etl_date") === odsMaxPartitionValue)

        //todo 抽取dim_user_info最新分区现有数据
        //todo 与抽取ods昨天的分区数据一样
        val dwdTable = spark.sql("select * from dwd.dim_user_info")
        val dwdMaxPartitionValue = dwdTable.select(max("etl_date")).first()(0)
        val dwdNewPartitionData = dwdTable.where(col("etl_date") === dwdMaxPartitionValue)

        //todo 由于ods库数据与dwd库数据的列顺序不一样
        //todo 即便spark会根据列名插入，还是建议手动对其 所以需要获取dwd的列
        //todo 如果要进行union，必须进行列对其
        val cols = dwdNewPartitionData.columns.map(col)

        //todo 按照题目要求，为ods库新增字段
        //todo 1. 填充空值
        val OdsData = odsNewPartitionData
            .withColumn("operate_time", greatest(col("operate_time"), col("create_time")))
            //todo 2. 插入新列
            .withColumn("dwd_insert_user", lit("user1"))
            .withColumn("dwd_insert_time", date_trunc("yyyy-MM-dd HH:mm:ss", current_timestamp()))
            .withColumn("dwd_modify_user", lit("user1"))
            .withColumn("dwd_modify_time", date_trunc("yyyy-MM-dd HH:mm:ss", current_timestamp()))
            //todo 3. 由于要union 必须进行列对其
            .select(cols:_*)

        //todo 我们无法确定dwd原始数据是否包含空，所以也进行空替换
        val DWDNewPartitionData = dwdNewPartitionData
            .withColumn("operate_time", greatest(col("operate_time"), col("create_time")))

        //todo 按operate_time排序，按id分组, 取最新，所以使用desc
        //todo win1是用来按照operate_time排序取最新一条
        //todo win2是为了取一坨数据中各种的需求值，不排序默认全部列更改
        val win1 = Window.partitionBy("id").orderBy(col("operate_time").desc)
        val win2 = Window.partitionBy("id")

        //todo 合并修改可以使用union 与 开窗完成
        val unTable = OdsData.union(DWDNewPartitionData)

        //todo 进行合并修改
        unTable
            //todo dwd_modify_time时间不变，使用min
            //todo dwd_insert_time存当前操作，使用max
            .withColumn("temp", row_number().over(win1))
            .withColumn("dwd_modify_time", min(col("dwd_modify_time")).over(win2))
            .withColumn("dwd_insert_time", max(col("dwd_insert_time")).over(win2))
            //todo 我们只需要最新的数据，所以取temp 为1
            .where(col("temp") === 1)
            .drop("temp")
            .write
            .format("hive")
            .mode(SaveMode.Append)
            .saveAsTable("dwd.dim_user_info")
    }
}
