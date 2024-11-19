import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.kafka.sink.{KafkaRecordSerializationSchema, KafkaSink, TopicSelector}
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

object 实时清洗1Ower {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root");
        val env = StreamExecutionEnvironment.getExecutionEnvironment

        val kafkas = KafkaSource.builder()
            .setTopics("ods_mall_data")
            .setGroupId("Kafka6666")
            .setBootstrapServers("192.168.45.13:9092")
            .setValueOnlyDeserializer(new SimpleStringSchema())
            .build();

        val kafkasink = KafkaSink.builder()
            .setBootstrapServers("192.168.45.13:9092")
            .setRecordSerializer(KafkaRecordSerializationSchema
                .builder()
                .setTopicSelector(new TopicSelector[String] {
                    override def apply(t: String): String = {
                        if(t.contains("order_detail_id")) {
                            return "fact_order_detail";
                        }
                        else{
                            return "fact_order_master";
                        }
                    }
                })
                .setKeySerializationSchema(new SimpleStringSchema())
                .setValueSerializationSchema(new SimpleStringSchema())
                .build()
            );

        val kafkaData = env.fromSource(kafkas, WatermarkStrategy.noWatermarks(), "kafka")
        .map(f =>{
            f.split("\"data\":")(1)
        }).map(f => f.substring(0,f.length-1))
        kafkaData.print();
        kafkaData.sinkTo(kafkasink.build());

        env.execute();
    }
}
