package _20250218

import com.google.gson.{Gson, JsonParser}
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.kafka.sink.{KafkaRecordSerializationSchema, KafkaSink, TopicSelector}
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

import java.util.Properties

object 清洗01 {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment

        //Source
        val ks = KafkaSource
                .builder()
                .setGroupId("r")
                .setTopics("ods_mall_data")
                .setBootstrapServers("192.168.45.13:9092")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build()


        //Sink 决定发往
        val ts = new TopicSelector[String] {
            override def apply(t: String): String = {
                if(t.contains("order_id"))
                    "fact_order_master"
                else
                    "fact_order_detail"
            }
        }


        //Sink
        val kss = KafkaRecordSerializationSchema
                .builder()
                .setKeySerializationSchema(new SimpleStringSchema())
                .setValueSerializationSchema(new SimpleStringSchema())
                .setTopicSelector(ts)
                .build()


        //Sink
        val c = new Properties()
        c.put("transaction.timeout.ms","7200000")
        val kk = KafkaSink
                .builder()
                .setKafkaProducerConfig(c)
                .setBootstrapServers("192.168.45.13:9092")
                .setRecordSerializer(kss)
                .build()


        env
                .fromSource(ks, WatermarkStrategy.noWatermarks(), "ks")
                .filter(f => f.contains("order_id") || f.contains("order_detail_id"))
                .map(f => JsonParser.parseString(f).getAsJsonObject.getAsJsonObject("data").toString)
                .sinkTo(kk)

        env.execute()
    }
}
