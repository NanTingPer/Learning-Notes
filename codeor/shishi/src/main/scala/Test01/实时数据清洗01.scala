package Test01

import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.kafka.sink.{KafkaRecordSerializationSchema, KafkaSink, TopicSelector}
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

object 实时数据清洗01 {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment

        val ks = KafkaSource.builder()
                .setBootstrapServers("192.168.45.10:9092")
                .setTopics("ods_mall_data")
                .setGroupId("awdffff")
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build()

        val ksink = KafkaSink.builder()
                .setBootstrapServers("192.168.45.10:9092")
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                .setValueSerializationSchema(new SimpleStringSchema())
                .setKeySerializationSchema(new SimpleStringSchema())
                .setTopicSelector(new TopicSelector[String] {
                    override def apply(t: String): String = {
                        if(t.contains("order_detail_id")) return "fact_order_detail"
                        if(t.contains("order_id")) return "fact_order_master"
                        "Test"

                    }
                }).build()).build()

        env.fromSource(ks,WatermarkStrategy.noWatermarks(),"kafkasource")
                .map(f =>{
                    val str = f.split("\"data\":")(1)
                    str.substring(0,str.length-1)
                }).sinkTo(ksink);

        env.execute()
    }
}
