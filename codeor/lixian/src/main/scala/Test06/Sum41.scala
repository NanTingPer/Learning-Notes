package Test06

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions._
import org.apache.spark.sql.types.DataTypes

import java.util.Properties

object Sum41 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession.builder().appName("hive").master("local[*]").enableHiveSupport().getOrCreate()
        //根据dwd层表统计在两天内连续下单并且下单金额保持增长的用户
        //userid            int
        //username      text
        //day                       text
        //totalconsumption double      訂單總金額
        //totalorder                                訂單總數
        val hiveData = "dwd.fact_order_info"
        val hive = spark.sql("select * from dwd.fact_order_info")

        //数据初步处理
        val aggdata = hive
            .withColumn("create_time", date_sub(col("create_time"), 0))
            .groupBy("user_id", "create_time")
            .agg(sum(col("final_total_amount")) as "totalconsumption", count("*") as "totalorder")

        val win1 = Window.partitionBy("user_id").orderBy("create_time")
        //不排序就是每一行都分配同一個最終結果，如果排序了那麼就是當前行分配的結果爲之前行到當前行的和
        val win2 = Window.partitionBy("user_id", "subData")
        val twoDay = aggdata
            .withColumn("tempcol", row_number().over(win1))
            .withColumn("subData", date_sub(col("create_time"), col("tempcol")))
            .withColumn("tempsum", count("*").over(win2))
            .where(col("tempsum") >= 2)
            .drop("tempcol", "subData")
//            .show
        val win3 = Window.partitionBy("user_id").orderBy("create_time")

        //过滤窗口(过滤多个两天的)
        val win4 = Window.partitionBy("user_id")
        val AllData = twoDay
            //lead取出給定偏移的指定列數據，是一個窗口函數
            //lag是上一行，lead是下一行
            .withColumn("downTotalconSumPtion", lead(col("totalconsumption"), 1).over(win3))
            //
            .withColumn("downTotalorder", lead(col("totalorder"), 1).over(win3))
            .withColumn("downCreate_time", lead(col("create_time"), 1).over(win3))
            .where(col("downTotalorder").cast(DataTypes.StringType) =!= lit("null") and (col("totalconsumption") - col("downTotalconSumPtion") < 0))
            .orderBy("user_id")
            //多个连续两天处理，将前一天的消费与当前日的消费相减，取最大
            .select("user_id", "create_time", "downCreate_time", "totalconsumption", "downTotalconSumPtion", "totalorder", "downTotalorder")
            .withColumn("subTotalconsumption", col("downTotalconSumPtion") - col("totalconsumption"))
            .withColumn("maxtol", max("subTotalconsumption").over(win4)) //用于后面取最大差值的那条数据(增长最高)
            .where(col("subTotalconsumption") === col("maxtol")) //取最大差值的数据(增长最高)
            .drop("maxtol", "subTotalconsumption") //删除计算列，保留数据列
            .withColumn("totalconsumption", col("totalconsumption") + col("downTotalconSumPtion")) //总消费金额
            .drop("downTotalconSumPtion")
            .withColumn("totalorder", col("totalorder") + col("downTotalorder")) //消费单数
            .drop("downTotalorder")
            .withColumn("day", concat(col("create_time"), lit("_"), col("downCreate_time"))) //day
            .drop("create_time", "downCreate_time")
//        13 18 181 182 268
        val conf = new Properties()
            conf.put("user","root")
            conf.put("password","123456")
        val user_info = spark.sql("select id, name from dwd.dim_user_info")
        AllData.join(user_info, AllData("user_id") === user_info("id"))
            .withColumnRenamed("name", "username")
            .select("user_id", "username", "day", "totalconsumption", "totalorder")
            .write
            .jdbc("jdbc:mysql://192.168.45.13/shtd_result?useSSL=false", "provinceavgcmp",conf)
    }
}
