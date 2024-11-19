import com.google.gson.Gson
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.kafka.sink.{KafkaRecordSerializationSchema, KafkaSink}
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

import java.text.SimpleDateFormat
import java.util.Date
import scala.util.Random

object 实时清洗2TwoOwer {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val env = StreamExecutionEnvironment.getExecutionEnvironment;

        val kafkaSource = KafkaSource.builder()
            .setTopics("ods_mall_log")
            .setBootstrapServers("192.168.45.13:9092")
            .setGroupId("kafakfk")
            .setValueOnlyDeserializer(new SimpleStringSchema())
            .build();

        val kafkaSink = KafkaSink.builder()
            .setBootstrapServers("192.168.45.13:9092")
            .setRecordSerializer(KafkaRecordSerializationSchema.builder()
            .setTopic("log_product_browse")
            .setValueSerializationSchema(new SimpleStringSchema())
            .setKeySerializationSchema(new SimpleStringSchema()).build())
            .build();

        env.fromSource(kafkaSource,WatermarkStrategy.noWatermarks(),"kafkasou")
            .filter(f => f.contains("product_browse"))
            .map(f =>{
                val ran = new Random()
                var i = ran.nextInt(10)
                val format = new SimpleDateFormat("MMddHHmmssSSS")
                var time = i + format.format(new Date(System.currentTimeMillis))
                var gson = new Gson();
                //(175 | 7777 | 0 | 0 | '20241108135659')
                val strings = f.replaceAll("product_browse:", "").replaceAll("['();]", "").replaceAll("数据发送开始:","").replaceAll("\\|", ",").split(",");
                gson.toJson(table(
                    time,
                    strings(0).toInt,strings(1).toInt,strings(2).toInt,strings(3),strings(4)
                ))
            }).sinkTo(kafkaSink);


        env.execute()

    }

    case class table(log_id: String,
                     product_id: Int,
                     customer_id: Int,
                     gen_order: Int,
                     order_sn: String,
                     modified_time: String)
}
