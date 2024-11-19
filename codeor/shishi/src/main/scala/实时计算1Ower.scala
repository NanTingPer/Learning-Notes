import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.functions.ReduceFunction
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.common.state.{MapState, MapStateDescriptor}
import org.apache.flink.api.common.typeinfo.{TypeInformation, Types}
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.streaming.api.functions.{KeyedProcessFunction, ProcessFunction}
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.$
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.util.Collector

import java.util
import javax.xml.crypto.{AlgorithmMethod, KeySelector, KeySelectorResult, XMLCryptoContext}
import javax.xml.crypto.dsig.keyinfo.KeyInfo

object 实时计算1Ower {
    def main(args: Array[String]): Unit = {
        //使用Flink 消费kafka 中log_product_browse 主题的数据，
        // 统计商品的UV（浏览用户量）和PV（商品浏览量），
        // 将结果写入HBase 中的表ads:online_uv_pv 中。
        // 使用Hive cli(没写错)查询ads.pv_uv_result
        // 表按照product_id 和pv 进行降序排序，查询出10 条数据，
        // 将结果截图粘贴至客户端桌面【Release\模块C 提交结果.docx】中对应的任务序号下；
        System.setProperty("HADOOP_USER_NAME", "root")
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        val envtable = StreamTableEnvironment.create(env);
        env.setParallelism(1)
        val kafkaSource = KafkaSource.builder()
            .setTopics("log_product_browse")
            .setBootstrapServers("192.168.45.13:9092")
            .setValueOnlyDeserializer(new SimpleStringSchema())
            .setGroupId("waf")
            .build();

        //product_id 商品ID
        //customer_id 用户ID
        //modified_time 202411093
        //{"log_id":"71118181231727","product_id":2051,"customer_id":12874,"gen_order":0,"order_sn":"0","modified_time":"20241109113426"}
        val DataStream = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "awf")
            .map(f => {
                val product_id = f.split(",")(1).replaceAll("[\" ]", "").split(":")(1)
                val customer_id = f.split(",")(2).replaceAll("[\" ]", "").split(":")(1)
                Tuple2(product_id.toInt, customer_id.toInt)
            }).keyBy(f => f._1)

            .process(new ProcessFunction[(Int, Int), (String, String, String,Int)] {
                var nums = 0;
                var mapState : MapState[Integer,(util.TreeSet[Integer],Integer)] = null;
                override def processElement(value: (Int, Int), ctx: ProcessFunction[(Int, Int), (String, String, String, Int)]#Context, out: Collector[(String, String, String, Int)]): Unit = {
                    nums+=1;
                    if(mapState.contains(value._1)) {
                        var unit = mapState.get(value._1) ;
                        val value1 = unit._1
                        var trSet = new util.TreeSet[Int]()
                        value1.forEach(f => trSet.add(f))
                        trSet.add(value._2)
                        var list = new util.ArrayList[Integer]()
                        trSet.forEach(f => list.add(f))
                        mapState.put(value._1,(list,unit._2+1))
                    }else{
                        var treeset = new util.ArrayList[Integer]()
                        treeset.add(value._2)
                        mapState.put(value._1,(treeset,1))
                    }
                    var uuuu = mapState.get(value._1)
                    out.collect(s"${value._1}",s"${uuuu._1.size()}",s"${uuuu._2}",nums)
                }
                override def open(parameters: Configuration): Unit = {
                    super.open(parameters)
                    mapState = getRuntimeContext.getMapState[Integer,(util.TreeSet[Integer],Integer)](new MapStateDescriptor(
                        "MapState",
                        Types.INT,
                        TypeInformation.of(classOf[(util.TreeSet[Integer],Integer)])))
                }
            })
        envtable.fromDataStream(DataStream,$("商品ID"),$("用户数量"),$("访问次数"),${"e"}).execute().print()

//        env.execute()
    }
}
