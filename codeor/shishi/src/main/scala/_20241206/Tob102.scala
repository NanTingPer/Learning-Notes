package _20241206

import com.google.gson.Gson
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.kafka.sink.{KafkaRecordSerializationSchema, KafkaSink, TopicSelector}
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

import java.text.SimpleDateFormat
import java.util.{Date, Random}

object Tob102 {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment

        val ks = KafkaSource.builder()
                .setBootstrapServers("192.168.45.13:9092")
                .setGroupId("awgfawg")
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setTopics("ods_mall_log")
                .build()

        val kk = KafkaSink.builder()
                .setBootstrapServers("192.168.45.13:9092")
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                .setValueSerializationSchema(new SimpleStringSchema())
                .setKeySerializationSchema(new SimpleStringSchema())
                .setTopic("log_product_browse").build())
                .build()


        env.fromSource(ks,WatermarkStrategy.noWatermarks(),"kss")
                .filter(f => f.contains("product_browse"))
                .map(f => {
                    //product_browse 2557 | 17770 | 0 | 0 | '20241129111419')
                    val strs = f.replaceAll("数据发送开始:", "").split("product_browse:\\(")(1).replaceAll("[':;)]", "").split("\\|")
                    val log_id = new Random().nextInt(10) + new SimpleDateFormat("MMddHHmmssSSS").format(new Date(System.currentTimeMillis()))
                    val gson = new Gson()
                    gson.toJson(tableinfo(log_id.toLong,strs(0),strs(1).toInt,strs(2).toInt,strs(3),strs(4)))
                }).sinkTo(kk)


        env.execute()
    }

    case class tableinfo(log_id:Long,
                         product_id:String,
                         customer_id : Int,
                         gen_order : Int,
                         order_sn : String,
                         modified_time : String)
}
