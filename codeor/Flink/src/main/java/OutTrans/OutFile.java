package OutTrans;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.OutputFileConfig;
import org.apache.flink.streaming.api.functions.sink.filesystem.bucketassigners.DateTimeBucketAssigner;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.time.ZoneId;
import java.util.Properties;

public class OutFile
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        //必须设置
        Env.enableCheckpointing(100, CheckpointingMode.EXACTLY_ONCE);

        KafkaSource<String> Source = KafkaSource
            .<String>builder()
            .setTopics("log_product_browse")
            .setBootstrapServers("192.168.45.10:9092")
            .setGroupId("kkkkk")
            .setStartingOffsets(OffsetsInitializer.earliest())
            .setValueOnlyDeserializer(new SimpleStringSchema())
            .build();

        DataStreamSource<String> KafkaSource = Env.fromSource(Source, WatermarkStrategy.noWatermarks(), "Kafwd66");

        FileSink<String> sink = FileSink
            //泛型方法
            .<String>forRowFormat(
                new Path("C:/temp"),
                new SimpleStringEncoder<>())
            //设置文件前缀和后缀
            .withOutputFileConfig(
                OutputFileConfig.builder()
                    //前缀
                    .withPartPrefix("run-")
                    //后缀
                    .withPartSuffix(".json")
                    .build())
            //按照目录分桶
            .withBucketAssigner(new DateTimeBucketAssigner<>("yyyy_MM_dd_HH", ZoneId.systemDefault()))
            //滚动策略
            .withRollingPolicy(
                DefaultRollingPolicy
                    .builder()
                    //设置多久一次 1分钟
                    .withRolloverInterval(Duration.ofMinutes(1))
                    //设置多大一次 1G
                    .withMaxPartSize(new MemorySize(1024*1024*1024))
                    .build())
            .build();

        KafkaSource.sinkTo(sink);

        Env.execute();
    }
}
