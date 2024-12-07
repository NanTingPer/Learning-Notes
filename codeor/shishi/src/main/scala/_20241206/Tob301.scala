package _20241206

import com.google.gson.JsonParser
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.table.api._
import org.apache.flink.util.Collector

import java.text.SimpleDateFormat
import java.util
import java.util.Date

object Tob301 {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        val envt = StreamTableEnvironment.create(env)
        val ks = KafkaSource.builder()
                .setBootstrapServers("192.168.45.13:9092")
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setTopics("log_product_browse")
                .setGroupId("awfaf")
                .build()

        val stream = env.fromSource(ks, WatermarkStrategy.noWatermarks(), "fff")
                .map(f => {
                    val jo = JsonParser.parseString(f).getAsJsonObject
                    val pid = jo.get("product_id").getAsString
                    val cid = jo.get("customer_id").getAsString
                    val mdtime = jo.get("modified_time").getAsString
                    new Tuple3(pid, cid, mdtime)
                })
                .keyBy(_._1)
                .window(TumblingProcessingTimeWindows.of(Time.seconds(60)))
                .process(new ProcessWindowFunction[(String, String, String), (Long, Long, Long, Long,String), String, TimeWindow] {
                    override def process(key: String, context: Context, elements: Iterable[(String, String, String)], out: Collector[(Long, Long, Long, Long,String)]): Unit = {
                        var pv = new util.TreeSet[String]
                        var uv = 0L
                        var modtime = 0L
                        val row_key = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())) + "-" + key
                        elements.foreach(f => {
                            pv.add(f._2)
                            uv += 1
                            if (f._3.toLong > modtime) modtime = f._3.toLong
                        })

                        out.collect(key.toLong, pv.size().toLong, uv, modtime,row_key)

                    }
                })

        val table = envt.fromDataStream(stream)
        envt.createTemporaryView("kt",table)

        envt.executeSql(
            """
              |create table ht(
              | row_key string,
              | info row<
              |     product_id bigint,
              |     product_name string,
              |     uv bigint,
              |     pv bigint,
              |     modified_time bigint
              | >) with (
              |     'connector'='hbase-2.2',
              |     'table-name'='ads:online_uv_pv',
              |     'zookeeper.quorum'='192.168.45.13:2181'
              |     )
              |
              |""".stripMargin)

        envt.executeSql(
            """
              |create table mt(
              | product_id bigint,
              | product_name string
              |) with (
              | 'connector'='jdbc',
              | 'url'='jdbc:mysql://192.168.45.13:3306/ds_db01?useSSL=false',
              | 'table-name'='product_info',
              | 'username'='root',
              | 'password'='123456'
              |)
              |""".stripMargin)

        envt.sqlQuery(
            """
              |with tt as(
              | select kt._1, mt.product_name, kt._2, kt._3, kt._4 ,kt._5
              | from kt as kt
              | join mt as mt
              | on kt._1 = mt.product_id
              |)
              |select _5 as row_key,row(_1, product_name, _2, _3, _4) as info from tt
              |""".stripMargin).executeInsert("ht")

//
//        envt.from("tt")
//                .select(concat(dateFormat(currentTimestamp(),"yyyy-MM-dd HH:mm:ss-").cast(DataTypes.STRING()),$"product_id") as "row_key",
//                    row(
//                        $"_1" as "product_id",
//                        $"product_name",
//                        $"_2" as "uv",
//                        $"_3" as "pv",
//                        $"_4" as "modified_time"
//                    ) as "info").executeInsert("ht")
//

    }

}
