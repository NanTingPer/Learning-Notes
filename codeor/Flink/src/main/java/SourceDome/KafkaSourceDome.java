package SourceDome;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class KafkaSourceDome
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<String> Kafka =
            KafkaSource.<String>builder()
                       //设置消费主题
                       .setTopics("Kafka666")
                       //设置Kafka链接
                       .setBootstrapServers("192.168.45.13:9092")
                       //设置反序列化器
                       .setValueOnlyDeserializer(new SimpleStringSchema())
                       //设置便宜 从最新开始
                       .setStartingOffsets(OffsetsInitializer.latest())
                       //设置消费者组
                       .setGroupId("KafkaSource")
                       //得到这个Source
                       .build();

        Env.fromSource(Kafka, WatermarkStrategy.noWatermarks(),"Kafka666").print();
        Env.execute();
    }
}
