package _20241220

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.{col, lit, row_number, window}
import org.apache.spark.sql.types.DataTypes

import java.util.{Properties, Random}

object jisuan01 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val sp = SparkSession.builder()
                .master("local[*]")
                .appName("ruawf")
                .config("hive.exec.scratchdir","hdfs://192.168.45.20:9000/user/temp/hive")
                .config("hive.exec.dynamic.partition.mode","nonstrict")
                .enableHiveSupport()
                .getOrCreate()

        sp.sql("select * from dwd.fact_order_master").createTempView("dwd")

        sp.sql(
            """
              |select * from dwd
              |where order_status = '已下单' or order_status = '已付款'
              |
              |""".stripMargin).createTempView("yxd")


        sp.sql(
            """
              |select province,count(order_status) as creat_order from yxd
              | where order_status = '已下单'
              | group by province
              |""".stripMargin).createTempView("yxds")

        sp.sql(
            """
              |select province,count(order_status) as payment from yxd
              | where order_status = '已付款'
              | group by province
              |""".stripMargin).createTempView("yfks")

        val table = sp.sql(
            """
              |select yx.province as province,creat_order,payment
              |from yxds as yx
              |join yfks as yf
              |on yx.province = yf.province
              |
              |""".stripMargin)

        val config = new Properties()
        config.put("user","default")
        config.put("password","123456")
//        config.put("driver","com.clickhouse.jdbc.DriverClickHouse")

        val win1 = Window.orderBy(col("payCVR").desc)
        table.withColumn("payCVR",lit((col("payment") - new Random().nextInt(200)) / col("creat_order")))
                .withColumn("payCVR",col("payCVR").cast(DataTypes.createDecimalType(8,3)))
                .withColumn("ranking",row_number().over(win1))
                .write.mode(SaveMode.Append).jdbc("jdbc:clickhouse://192.168.45.20:8123/ds_result","payment_cvr",config)




//        sp.sql(
//            """
//              |with yxdd as(
//              |select province,count(order_status) as creat_order from yxd
//              | where creat_order = '已下单'
//              | group by province),
//              |yfk as(
//              |select province,count(order_status) as payment from yxd
//              | where creat_order = '已付款'
//              | group by province)
//              |
//              |""".stripMargin).show

    }
}
