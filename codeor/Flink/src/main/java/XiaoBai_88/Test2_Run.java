package XiaoBai_88;

import com.google.gson.Gson;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class Test2_Run
{



// 使用Kafka 自带的消费者消费log_product_browse（Topic）的前1条数据，
// 将结果截图粘贴至客户端桌面【Release\模块C 提交结果.docx】中对应的任务序号下。
    public static void main(String[] args) throws Exception
    {
        //TODO 这是一套你在小白上看不到的代码

        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();
        //TODO 精确一次
//        Env.enableCheckpointing(5000, CheckpointingMode.EXACTLY_ONCE);

        //2、使用Flink 消费Kafka 中topic 为ods_mall_log 的数据，
        //TODO 就是使用数据源是Kafka的topic ods_mall_log
        KafkaSource<String> kafkaSource = KafkaSource
            .<String>builder()
                .setTopics("ods_mall_log")
                .setGroupId("KafkaSourceLog_Task2")
                .setBootstrapServers("192.168.45.13:9092")
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        KafkaSink<String> kafkaSink = KafkaSink
            .<String>builder()
            .setBootstrapServers("192.168.45.13:9092")
            .setRecordSerializer(KafkaRecordSerializationSchema
                .<String>builder()
                .setKeySerializationSchema(new SimpleStringSchema())
                .setValueSerializationSchema(new SimpleStringSchema())
                .setTopic("log_product_browse")
                .build())
//            .setTransactionalIdPrefix("-sel")
            //TODO 事务超时时间 需要小于15min 大于Checkpointing
//            .setProperty(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG,"10000")
            .build();

        SingleOutputStreamOperator<String> kafkaData = Env
            .fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "KafkaSource")
            .uid("KafkaSource")
            .name("KafkaSource");

        //// 根据数据中不同的表前缀区分，过滤出product_browse 的数据，
        //TODO 数据有两种 直接使用字符串自带的判断包含就行了
        SingleOutputStreamOperator<String> SinkData = kafkaData.filter(f -> f.contains("product_browse"));

        //TODO 小白都转成JSON 那就转呗
        //TODO 5438|11696|0|0|'20230419193256');
        //TODO 确一列  log_id	long	自增长id	可以使用随机数（0-9）+MMddHHmmssSSS 代替
        Random ran = new Random();
        SinkData.map(f -> {
            //TODO 构建log_id
            int i = ran.nextInt(10);//右开
            SimpleDateFormat format = new SimpleDateFormat("MMddHHmmssSSS");
            StringBuilder log_id = new StringBuilder();
            log_id.append(i).append("+")
                .append(format.format(new Date(System.currentTimeMillis())));


            //TODO 割分与重组
            ///log_id	        long
            ///product_id	    string
            ///customer_id	    int
            ///gen_order	    int
            ///order_sn	    string
            ///modified_time	timestamp
            String[] s = f
                .replaceAll("product_browse:\\(", "")
                .replaceAll("\\)","")
                .split("\\|");

            StringBuilder Json = new StringBuilder();
            //{"order_id":1,"order_sn":"2024110658009104","modified_time":"2024-11-01 18:05:40"}
            Json.append("{")
                .append(Rstring("log_id",log_id.toString(),1))
                .append(Rstring("product_id",s[0],1))
                .append(Rstring("customer_id",s[1],1))
                .append(Rstring("gen_order",s[2],1))
                .append(Rstring("order_sn",s[3],1))
                .append(Rstring("modified_time",s[4],0))
                .append("}");
            return Json.toString().replaceAll("[;']","").replaceAll("数据发送开始:","");
        }).sinkTo(kafkaSink); //TODO 直接写了


        // 将数据分别分发至kafka的DWD 层log_product_browse 的Topic 中，其分区数为2，其他的表则无需处理。
        //TODO 在Kafka创建一个Topic
        //TODO kafka-topics.sh --bootstrap-server 192.168.45.13:9092 --create --topic log_product_browse --partitions 2
        //TODO 分发
//        KafkaSink<String> kafkaSink = KafkaSink
//            .<String>builder()
//                .setBootstrapServers("192.168.45.13:9092")
//                    .setRecordSerializer(KafkaRecordSerializationSchema
//                        .<String>builder()
//                        .setKeySerializationSchema(new SimpleStringSchema())
//                        .setValueSerializationSchema(new SimpleStringSchema())
//                        .setTopic("log_product_browse")
//                        .build())
//            .setTransactionalIdPrefix("-sel")
//            //TODO 事务超时时间 需要小于15min 大于Checkpointing
//            .setProperty(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG,"10000")
//            .build();



        Env.execute();
    }

    private static String Rstring(String str,String value,int xd)
    {
        if(xd == 1) {
            return "\"" + str + "\"" + ":" + "\"" + value + "\",";
        }else {
            return "\"" + str + "\"" + ":" + "\"" + value + "\"";
        }
    }
}
