package OldJobTest

import com.esotericsoftware.kryo.serializers.DefaultSerializers.StringSerializer
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.kafka.sink.{KafkaRecordSerializationSchema, KafkaSink, KafkaSinkBuilder, TopicSelector}
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

object 实时清洗4_1Ower {
        def main(args: Array[String]): Unit = {
                System.setProperty("HADOOP_USER_NAME","root")

                val env = StreamExecutionEnvironment.getExecutionEnvironment
                env.setParallelism(1);

                //创建Kafka数据源
                val KafkaS = KafkaSource.builder()
                    .setBootstrapServers("192.168.45.10:9092")
                    .setTopics("ods_mall_data")
                    .setValueOnlyDeserializer(new SimpleStringSchema())
                    .setGroupId("kafka666")
                    .build();

                //创建输出目标
                val kafkasink: KafkaSinkBuilder[String] = KafkaSink.builder()
                        .setBootstrapServers("192.168.45.10:9092")
                        .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                                .setValueSerializationSchema(new SimpleStringSchema())
                                .setKeySerializationSchema(new SimpleStringSchema())
                                .setTopicSelector(new TopicSelector[String] {
                                        override def apply(t: String): String = {
                                                if (t.contains("order_id")) {
                                                        "fact_order_master";
                                                } else {
                                                        "fact_order_detail"
                                                }
                                        }
                                })
                                .build())

                //按照具体要求对数据进行处理
                env.fromSource(KafkaS,WatermarkStrategy.noWatermarks(),"kafkaSource")
                    .map(f =>{
                        val str = f.split("data\":")(1)
                        val str2 = str.substring(0,str.length-1);
                        str2;
                    }).sinkTo(kafkasink.build());


                env.execute()

    }
}
