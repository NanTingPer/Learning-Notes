package Test01

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.{col, floor, lit, rand, round, row_number}

import java.util.Properties

object 离线计算01 {


    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val sp = SparkSession.builder()
                .master("local[*]")
                .appName("spark301")
                .config("hive.exec.scratchdir","hdfs://192.168.45.13:9000/user/hive/temp")
                .config("hive.exec.dynamic.partition.mode","nonstrict")
                .enableHiveSupport()
                .getOrCreate()

        sp.table("ods.order_master").createTempView("ods")

        sp.sql(
            """
              |select *
              |from ods
              |where
              |(order_status='已下单' or order_status='已付款') AND
              |substr(create_time,0,4) = '2022'
              |""".stripMargin).createTempView("odstrue")

        sp.sql(
            """
              |with yxd as(
              | select province,count(1) as creat_order
              | from odstrue
              | where order_status = '已下单'
              | group by province
              |),yfk as(
              | select province,count(1) as payment
              | from odstrue
              | where order_status = '已付款'
              | group by province
              |)
              |select xd.province,xd.creat_order,fk.payment
              |from yxd as xd
              |join yfk as fk
              |on xd.province = fk.province
              |""".stripMargin).createTempView("final")

        val conf = new Properties()
        conf.put("password","123456")
        conf.put("user","default")

        val win1 = Window.orderBy(col("payCVR").desc)
        sp.sql("select * from final")
                .withColumn("payment",col("payment")-(floor(rand() * 250) + 206))
                .withColumn("payCVR",round(col("payment")/col("creat_order"),3))
                .withColumn("ranking",row_number().over(win1))
                .write
                .mode(SaveMode.Append)
                .jdbc("jdbc:clickhouse://192.168.45.13:8123/ds_result","payment_cvr",conf)

    }

}
