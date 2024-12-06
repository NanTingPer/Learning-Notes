package Test01

import com.google.gson.Gson
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.kafka.sink.{KafkaRecordSerializationSchema, KafkaSink}
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

import java.text.SimpleDateFormat
import java.util.{Date, Random}

object 实时数据清洗02 {
    def main(args: Array[String]): Unit = {
        val environment = StreamExecutionEnvironment.getExecutionEnvironment

        val ks = KafkaSource.builder()
                .setBootstrapServers("192.168.45.10:9092")
                .setTopics("ods_mall_log")
                .setGroupId("awffff")
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build()

        val ksink = KafkaSink.builder()
                .setBootstrapServers("192.168.45.10:9092")
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                .setValueSerializationSchema(new SimpleStringSchema())
                .setKeySerializationSchema(new SimpleStringSchema())
                .setTopic("log_product_browse").build())
                .build()

        //product_browse //todo: 218 | 1549 | 0 | 0 |'20241127170955')
        environment.fromSource(ks,WatermarkStrategy.noWatermarks(),"fffff")
                .filter(f => f.contains("product_browse"))
                .map(f =>{
                    val strs = f.replaceAll("数据发送开始:","").replaceAll("product_browse:\\(", "").replaceAll("[);']", "").split("\\|")
                    val log_id = new Random().nextInt(10) + new SimpleDateFormat("MMddHHmmssSSS").format(new Date(System.currentTimeMillis()))
                    val gson = new Gson()
                    gson.toJson(new tableInfo(log_id,
                        strs(0),
                        strs(1).toInt,
                        strs(2).toInt,
                        strs(3),
                        strs(4),
                    ))
                }).sinkTo(ksink)


        environment.execute()
    }

    case class tableInfo(log_id : String,product_id : String, customer_id : Int, gen_order : Int, order_sn :String,modified_time :String)
}
