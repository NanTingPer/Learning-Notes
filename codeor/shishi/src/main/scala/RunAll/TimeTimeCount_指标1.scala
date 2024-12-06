//package RunAll
//
//import com.google.gson.JsonParser
//import org.apache.flink.api.common.eventtime.WatermarkStrategy
//import org.apache.flink.api.common.serialization.SimpleStringSchema
//import org.apache.flink.api.scala.createTypeInformation
//import org.apache.flink.connector.kafka.source.KafkaSource
//import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
//import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
//import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
//import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows
//import org.apache.flink.streaming.api.windowing.time.Time
//import org.apache.flink.streaming.api.windowing.windows.TimeWindow
//import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
//import org.apache.flink.util.Collector
//
//import java.text.SimpleDateFormat
//import java.util._
//
//object TimeTimeCount_指标1 {
//    def main(args: Array[String]): Unit = {
//        val env = StreamExecutionEnvironment.getExecutionEnvironment
//        val envtable = StreamTableEnvironment.create(env)
//
//        val ks = KafkaSource.builder()
//                .setBootstrapServers("192.168.45.10:9092")
//                .setTopics("log_product_browse")
//                .setGroupId("kkkkkawfa")
//                .setStartingOffsets(OffsetsInitializer.earliest())
//                .setValueOnlyDeserializer(new SimpleStringSchema())
//                .build()
//
//        val stream = env.fromSource(ks, WatermarkStrategy.noWatermarks(), "awfawf")
//                .map(f => {
//                    val json = JsonParser.parseString(f).getAsJsonObject
//                    val pid = json.get("product_id").getAsString
//                    val cid = json.get("customer_id").getAsString
//                    Tuple2(pid, cid)
//                })
//                .keyBy(_._1)
//                .window(TumblingProcessingTimeWindows.of(Time.seconds(60)))
//                //TODO 输入 string string
//                //TODO 输出 string string string string
//                //TODO Key string
//                //TODO WindowType TimeWindow
//                .process(new ProcessWindowFunction[(String, String), (String, Long, Long, Long), String, TimeWindow] {
//                    override def process(key: String, context: Context, elements: Iterable[(String, String)], out: Collector[(String, Long, Long, Long)]): Unit = {
//                        val UV = new util.TreeSet[String]()
//                        var PV = 0
//                        val inter = elements.iterator
//                        while (inter.hasNext) {
//                            val tuple = inter.next()
//                            UV.add(tuple._2)
//                            PV += 1
//                        }
//                        val row_key = new Random().nextInt(10) + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date(System.currentTimeMillis()))
//                        out.collect(row_key, key.toLong, UV.size().toLong, PV.toLong)
//                    }
//                })
//
//        val table = envtable.fromDataStream(stream)
//        envtable.createTemporaryView("kafka",table);
//
//        envtable.executeSql(
//            """
//              |create table HbaseT(
//              | row_key string,
//              | info row<
//              |     product_id bigint,
//              |     product_name string,
//              |     uv bigint,
//              |     pv bigint>
//              |) with (
//              | 'connector'='hbase-2.2',
//              | 'table-name'='ads:online_uv_pv',
//              | 'zookeeper.quorum'='192.168.45.10:2181'
//              |)
//              |""".stripMargin)
//
//        envtable.executeSql(
//            """
//              |create table mysql(
//              | product_id bigint,
//              | product_name string
//              |) with (
//              | 'connector'='jdbc',
//              | 'url'='jdbc:mysql://192.168.45.10:3306/ds_db01?uerSSL=false',
//              | 'table-name'='product_info',
//              | 'username'='root',
//              | 'password'='123456'
//              |)
//              |""".stripMargin)
//
//        envtable.sqlQuery(
//            """
//              |with temp as(
//              |select ka._1, ka._2, my.product_name, ka._3, ka._4
//              |from mysql as my
//              |join
//              |     kafka as ka
//              |on
//              |     my.product_id = ka._2
//              |)
//              |select _1 as row_key,
//              |     row(_2,product_name,_3,_4) as info
//              |from temp
//              |""".stripMargin).executeInsert("HbaseT")
//
//
//    }
//}
