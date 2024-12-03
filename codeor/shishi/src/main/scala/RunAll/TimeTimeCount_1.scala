package RunAll

import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.kafka.sink.{KafkaRecordSerializationSchema, KafkaSink, KafkaSinkBuilder, TopicSelector}
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

object TimeTimeCount_1 {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        val ks = KafkaSource.builder()
                .setBootstrapServers("192.168.45.10:9092")
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setTopics("ods_mall_data")
                .setGroupId("kafkafafwf")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .build()

        val sinkkafa: KafkaSinkBuilder[String] = KafkaSink.builder()
                .setBootstrapServers("192.168.45.10:9092")
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setValueSerializationSchema(new SimpleStringSchema())
                        .setKeySerializationSchema(new SimpleStringSchema())
                        .setTopicSelector(new TopicSelector[String] {
                            override def apply(t: String): String = {
                                if (t.contains("order_id")) return "fact_order_master"
                                "fact_order_detail "
                            }
                        }).build())

        val stream = env.fromSource(ks, WatermarkStrategy.noWatermarks(), "kafkasi")
                .map(f => {
                    val str = f.split("\"data\":")(1)
                    str.substring(0, str.length - 1)
                })
        stream.sinkTo(sinkkafa.build())



        env.execute()
    }
}
