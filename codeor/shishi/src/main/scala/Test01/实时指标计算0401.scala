package Test01

import com.google.gson.{Gson, JsonParser}
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.streaming.runtime.watermarkstatus.WatermarkStatus
import org.apache.flink.table.api.{AnyWithOperations, FieldExpression}
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.table.api._
import org.apache.flink.util.Collector

import java.util

/**
 * 使用Flink 消费kafka 中log_product_browse 主题的数据，
 * 统计商品的UV（浏览用户量）和PV（商品浏览量），
 * 将结果写入HBase 中的表ads:online_uv_pv 中。
 * 使用Hive cli(没写错)查询ads.pv_uv_result 表按照product_id 和pv 进行降序排序，
 * 查询出10 条数据
 */
object 实时指标计算0401 {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        val envt = StreamTableEnvironment.create(env)

        val ks = KafkaSource.builder()
                .setBootstrapServers("192.168.45.10:9092")
                .setTopics("log_product_browse")
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setGroupId("awf")
                .build()

        val kstr = env.fromSource(ks, WatermarkStrategy.noWatermarks(), "kss")
                .map(f => {
                    val jo = JsonParser.parseString(f).getAsJsonObject
                    val spid = jo.get("product_id").getAsString
                    val usr_id = jo.get("customer_id").getAsString
                    val mod_time = jo.get("modified_time").getAsString
                    (spid, usr_id, mod_time)
                })
                .keyBy(_._1)
                .window(TumblingProcessingTimeWindows.of(Time.seconds(60)))
                .process(new ProcessWindowFunction[(String, String, String), (Long, Long, Long, Long), String, TimeWindow] {
                    override def process(key: String, context: Context, elements: Iterable[(String, String, String)], out: Collector[(Long, Long, Long, Long)]): Unit = {
                        var pv = new util.TreeSet[String]
                        var uv = 0
                        var maxtime = 0L
                        elements.foreach(f => {
                            pv.add(f._2)
                            uv += 1;
                            if (f._3.toLong > maxtime) maxtime = f._3.toLong
                        })
                        out.collect(key.toLong, pv.size().toLong, uv.toLong, maxtime)
                    }
                })

        val kstable = envt.fromDataStream(kstr)

        envt.createTemporaryView("kt",kstable)

        envt.executeSql(
            """
              |create table hbtable(
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
              |     'zookeeper.quorum'='192.168.45.10:2181'
              | )
              |
              |
              |""".stripMargin)

        envt.executeSql(
            """
              |create table mysqlt(
              | product_id bigint,
              | product_name string
              |) with (
              | 'connector'='jdbc',
              | 'url'='jdbc:mysql://192.168.45.10:3306/ds_db01?useSSL=false',
              | 'username'='root',
              | 'password'='123456',
              | 'table-name'='product_info'
              |)
              |""".stripMargin)

//        envt.sqlQuery("select * from kt").execute().print()
        // $"_1" as "product_id",
        // $"_2" as "pv",
        // $"_3" as "uv",
        // $"_4" as "time"
        val temptable= envt.sqlQuery(
            """
              |with
              |     ftb as(
              |select
              |     *
              |from
              |     kt as k
              |join
              |     mysqlt as m
              |on
              |     k._1 = m.product_id)
              |select * from ftb
              |""".stripMargin)
        envt.createTemporaryView("ftb",temptable)

        val table2 = envt.sqlQuery(
            """
              |with r as(
              |select
              |       _1 as product_id,
              |       product_name,
              |       _2 as pv,
              |       _3 as uv,
              |       _4 as modified_time
              |from ftb
              |)
              |select * from r
              |""".stripMargin)
        envt.createTemporaryView("r",table2)


        envt.from("r")
                .select(concat(randInteger(10).cast(DataTypes.STRING),dateFormat(currentTimestamp(),"yyyyMMddHHmmssSSS").cast(DataTypes.STRING())) as "row_key",
                    row(
                        $"product_id",
                        $"product_name",
                        $"pv",
                        $"uv",
                        $"modified_time"
                    ) as "info").executeInsert("hbtable")

    }
}
