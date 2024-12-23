import com.google.gson.JsonParser
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.kafka.sink.{KafkaRecordSerializationSchema, KafkaSink, TopicSelector}
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

object qinxi01 {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment

        val ks = KafkaSource.builder()
                .setBootstrapServers("192.168.45.13:9092")
                .setTopics("ods_mall_data")
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setGroupId("awff")
                .build()

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
                }).build()).build()

        env.fromSource(ks,WatermarkStrategy.noWatermarks(),"ksss")
                .filter(f => f.contains("order_id") || f.contains("order_detail_id"))
                .map(f => JsonParser.parseString(f).getAsJsonObject.getAsJsonObject("data").toString)
                .sinkTo(ksink)


        env.execute()
    }
}
