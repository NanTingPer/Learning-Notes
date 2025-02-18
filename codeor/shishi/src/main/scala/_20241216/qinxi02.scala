package _20241216

import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.kafka.sink.{KafkaRecordSerializationSchema, KafkaSink, TopicSelector}
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

object qinxi02 {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment

        val ks = KafkaSource.builder()
                .setBootstrapServers("192.168.45.13:9092")
                .setTopics("ods_mall_log")
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setGroupId("awff")
                .build()

//        customer_login_log : ( 16298 | '20241212014843 '| '218.66 .101 .115 '| 1 )


        val ksink = KafkaSink.builder()
                .setBootstrapServers("192.168.45.13:9092")
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setValueSerializationSchema(new SimpleStringSchema())
                        .setKeySerializationSchema(new SimpleStringSchema())
                        .setTopicSelector(new TopicSelector[String] {
                            override def apply(t: String): String = {
                                if(t.contains("order_id")) return "fact_order_master"
                                return "fact_order_detail"
                            }
                        })
                        .build())
                .build()

        env.fromSource(ks,WatermarkStrategy.noWatermarks(),"wfawf")
                .filter(f => f.contains("customer_login_log"))
//                .map(f => {
//                    f.replaceAll("customer_login_log:\\(","").replaceAll("[');]","").split("")
//                })
                .sinkTo(ksink);
        env.execute()
    }
}
