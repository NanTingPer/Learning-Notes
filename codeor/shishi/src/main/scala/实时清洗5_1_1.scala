import com.google.gson.Gson
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.kafka.sink.{KafkaRecordSerializationSchema, KafkaSink}
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

import java.text.SimpleDateFormat
import java.util.{Date, Random}

object 实时清洗5_1_1 {
        def main(args: Array[String]): Unit = {
                val env = StreamExecutionEnvironment.getExecutionEnvironment

                val kafk = KafkaSource.builder()
                        .setBootstrapServers("192.168.45.10:9092")
                        .setTopics("ods_mall_log")
                        .setValueOnlyDeserializer(new SimpleStringSchema())
                        .setGroupId("kafka666")
                        .build()

                val kafksink = KafkaSink.builder()
                        .setBootstrapServers("192.168.45.10:9092")
                        .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setValueSerializationSchema(new SimpleStringSchema())
                        .setKeySerializationSchema(new SimpleStringSchema())
                        .setTopic("log_product_browse")
                        .build())
                        .build();

                env.fromSource(kafk,WatermarkStrategy.noWatermarks(),"kafka666666")
                        .filter(f => f.contains("product_browse"))
                        //product_browse //product_browse:( 9778 | 3574 | 0 | 0 | '20241121214549
                        .map(f => {
                                val strs = f.split("\\(")(1).replaceAll("[:';)]", "").split("\\|")
                                val log_id = new Random().nextInt(10) + "+" + new SimpleDateFormat("MMddHHmmssSSS").format(new Date(System.currentTimeMillis()))
                                val gson = new Gson()
                                gson.toJson(new tableview(log_id,
                                        strs(0),
                                        strs(1).toInt,
                                        strs(2).toInt,
                                        strs(3),
                                        strs(4)))
                        }).sinkTo(kafksink)

                env.execute()
        }

        case  class tableview(log_id : String,
                              product_id : String,
                              customer_id : Int,
                              gen_order : Int,
                              order_sn : String,
                              modified_time : String)
}
