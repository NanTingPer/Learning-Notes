package OutTrans;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchemaBuilder;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

public class OutKafka
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        ///如果是精准一次必须设置
        ///设置了精准一次必须指定事务前缀
        ///必须指定事务超时时间
        Env.enableCheckpointing(500, CheckpointingMode.EXACTLY_ONCE);

        DataStreamSource<String> SocketSource = Env.socketTextStream("192.168.45.13", 7777);


        KafkaSink<String> SinkKafka = KafkaSink.<String>builder()
                   .setBootstrapServers("192.168.45.13:9092")
                   .setRecordSerializer(KafkaRecordSerializationSchema
                       .builder()
                       //设置Key序列化器
                       .setKeySerializationSchema(new SimpleStringSchema())
                       //设置Value序列化器
                       .setValueSerializationSchema(new SimpleStringSchema())
                       //设置要写入的Topic名称
                       .setTopic("Kafka666")
                       .build())
                   //精准一次
                   .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
                   //设置事务ID前缀
                   .setTransactionalIdPrefix("Kafka")
                   //设置事务超时时间
                   //事务超时时间 小于15分 大于上面设置的
                   .setProperty(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, 10 * 60 * 1000 + "")
                   .build();

        DataStreamSink<String> run = SocketSource.sinkTo(SinkKafka);

        Env.execute();
    }
}
