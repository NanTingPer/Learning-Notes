package Test02

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
import org.apache.flink.table.api._
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.util.Collector

import java.util

object 实时计算1 {
    def main(args: Array[String]): Unit = {
        //TODO  使用Flink 消费kafka 中的数据，
        //TODO  统计商品的UV 和PV，
        //TODO  将结果写入HBase 中的表ads：online_uv_pv 中。
        //TODO fact_order_detail
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        val envt = StreamTableEnvironment.create(env)

        val ks = KafkaSource.builder()
                .setBootstrapServers("192.168.45.13:9092")
                .setTopics("fact_order_detail")
                .setGroupId("kf66")
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build()

        envt.executeSql(
            """
              |create table tb(
              | row_key string,
              | info row<
              |     product_id string,
              |     uv string,
              |     pv string
              | >
              |) with (
              | 'connector'='hbase-2.2',
              | 'table-name'='ads:online_uv_pv',
              | 'zookeeper.quorum'='192.168.45.13:2181'
              |)
              |
              |""".stripMargin)

        val stream = env.fromSource(ks, WatermarkStrategy.noWatermarks(), "fwff")
        .map(f => {
            val json = JsonParser.parseString(f).getAsJsonObject
            val pid = json.get("product_id").getAsString
            pid
        })
        .keyBy(f => f)
        .window(TumblingProcessingTimeWindows.of(Time.seconds(60)))
        .process(new ProcessWindowFunction[String, (String,String, String), String, TimeWindow] {
            override def process(key: String, context: Context, elements: Iterable[String], out: Collector[(String,String, String)]): Unit = {
                var uv = new util.TreeSet[String]
                var pv = 0L;
                elements.foreach(f => {
                    uv.add(f)
                    pv += 1
                })
                out.collect(key,uv.size().toString, pv.toString)
            }
        })

        val e = envt.fromDataStream(stream)
        envt.createTemporaryView("st",e)

        envt.from("st")
                .select(concat(randInteger(10).cast(DataTypes.STRING()),dateFormat(currentTimestamp(),"yyyyMMddHHmmssSSS")) as "row_key",
                    row(
                        $"_1" as "product_id",
                        $"_2" as "uv",
                        $"_3" as "pv"
                    ) as "info").executeInsert("tb")

    }
}
