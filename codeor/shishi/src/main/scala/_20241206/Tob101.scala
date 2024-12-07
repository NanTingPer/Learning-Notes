package _20241206

import com.google.gson.JsonParser
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.kafka.sink.{KafkaRecordSerializationSchema, KafkaSink, TopicSelector}
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

object Tob101 {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        val ks = KafkaSource.builder()
                .setBootstrapServers("192.168.45.13:9092")
                .setGroupId("awgfawg")
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setTopics("ods_mall_data")
                .build()

        val kk = KafkaSink.builder()
                .setBootstrapServers("192.168.45.13:9092")
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                .setValueSerializationSchema(new SimpleStringSchema())
                .setKeySerializationSchema(new SimpleStringSchema())
                .setTopicSelector(new TopicSelector[String] {
                    override def apply(t: String): String = {
                        if(t.contains("order_id")) return "fact_order_master"
                        return "fact_order_detail"
                    }
                }).build())
                .build()

        env.fromSource(ks,WatermarkStrategy.noWatermarks(),"kfff")
                .filter(f => f.contains("order_master") || f.contains("order_detail_id"))
                .map(f =>{
                    JsonParser.parseString(f).getAsJsonObject.getAsJsonObject("data").toString
                }).sinkTo(kk)


        env.execute()
    }
}
