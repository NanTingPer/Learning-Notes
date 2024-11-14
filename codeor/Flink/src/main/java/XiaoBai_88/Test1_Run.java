package XiaoBai_88;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.sink.TopicSelector;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.SideOutputDataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

public class Test1_Run
{
    public static void main(String[] args) throws Exception
    {
        //TODO 这是一套你在小白上看不到的代码
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<String> kafkaSource =
            KafkaSource
               .<String>builder()
               .setBootstrapServers("192.168.45.13:9092")
               .setTopics("ods_mall_data")
               .setGroupId("Kafka6666")
               .setValueOnlyDeserializer(new SimpleStringSchema())
               .build();


        OutputTag<String> detail = new OutputTag<>("detail",Types.STRING);
        OutputTag<String> master = new OutputTag<>("master",Types.STRING);

        SingleOutputStreamOperator<String> process = Env
            .fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "kafkaData", Types.STRING)
            .uid("kafkaSource")
            .name("KafkaSource")
            .process(new ProcessFunction<String, String>()
            {
                @Override
                public void processElement(String value, ProcessFunction<String, String>.Context ctx, Collector<String> out) throws Exception
                {
                    if (value.contains("fact_order_detail")) ctx.output(detail, value);
                    if (value.contains("fact_order_master")) ctx.output(master, value);
                }
            });

        KafkaSink<String> Sink = SinkTopicRun(new TopicSelector<String>() {
            @Override
            public String apply(String s)
            {
               if(s.contains("fact_order_detail"))
               {
                   return "fact_order_detail";
               }else
               {
                   return "fact_order_master";
               }
            }
        });


        SideOutputDataStream<String> sideOutput = process.getSideOutput(detail);
        SideOutputDataStream<String> sideOutput1 = process.getSideOutput(master);
        DataStreamToSink(sideOutput,Sink);
        DataStreamToSink(sideOutput1,Sink);

        Env.execute();
    }

    public static KafkaSink<String> SinkTopicRun(TopicSelector<String> FUN){
        KafkaSink<String> Sink = KafkaSink.<String>builder()
                .setBootstrapServers("192.168.45.13:9092")
                .setRecordSerializer(KafkaRecordSerializationSchema
                    .builder()
                    //设置Key序列化器
                    .setKeySerializationSchema(new SimpleStringSchema())
                    //设置Value序列化器
                    .setValueSerializationSchema(new SimpleStringSchema())
                    //设置要写入的Topic名称
                    .setTopicSelector(FUN)
                    .build()).build();
        return Sink;
    }

    public static void DataStreamToSink(SideOutputDataStream<String> str, Sink<String> sink)
    {
        SingleOutputStreamOperator<String> map = str
            .map(f -> f.split("\"data\":\\{")[1])
            .map(f -> f.replaceAll("}}", ""));

        map.sinkTo(sink);
    }
}
