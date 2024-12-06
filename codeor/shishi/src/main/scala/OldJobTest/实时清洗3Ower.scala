package OldJobTest

import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.java.tuple.Tuple23
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.api.scala.typeutils.Types
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.table.api.Schema
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

import java.time.format.DateTimeFormatter
import java.util

object 实时清洗3Ower {
    def main(args: Array[String]): Unit = {
        //在任务1，2进行的同时，需要将fact_order_master、fact_order_detail、dim_customer_login_log备份至HBase中，
        // rowkey使用随机数（0-9）+yyyyMMddHHmmssSSS，
        // 其中对于customer_login_log缺失主键，请用随机数（0-9）+用户id+登陆时间代替)。
        // 使用HBase Shell 查看ods:table1 表的任意2 条数据，
        // 查看字段为key1 与key2、查看ods:table2 表的任意2 条数据，
        // 查看字段为key1与key2、查看ods:table3 表的任意2 条数据，
        // 查看字段为key1 与 key2。
        // 将结果分别截图粘贴至客户端桌面【Release\模块C 提交结果.docx】中对应的任务序号下。

        val topMas = "fact_order_master"
        val topDet = "fact_order_detail"
        val topLog = "dim_customer_login_log"
        val ser = "192.168.45.13:9092"

        System.setProperty("HADOOP_USER_NAME","root")
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        val envT = StreamTableEnvironment.create(env)

        env.setParallelism(1)

        val value = KafkaSource.builder()
            .setTopics(topLog)
            .setGroupId("kfff")
            .setBootstrapServers(ser)
            .setValueOnlyDeserializer(new SimpleStringSchema())
            .build()

        val data = env.fromSource(value, WatermarkStrategy.noWatermarks(), "kafka")

        val dataStream = datr(data,1)
        val tp23 = datr(data, 0).executeAndCollect(10)
        tp23.foreach(s => println(s))
    }

    def datr(data:DataStream[String],spunm:Int):DataStream[Tuple23[String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String]] ={
        val value = data.filter(f => true)
            .map(f => {
                var tup23 = new Tuple23[String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String]()
                val strings = f.replaceAll("[\"}{]", "").split(",")
                var num = 0
                while (num < strings.size) {
                    var str = strings(num)
                    try {
                        tup23.setField(str.split(":")(spunm),num);
                    } catch {
                        case e: Exception =>
                    }
                    num += 1
                }
                tup23
            })
        value
    }
}
