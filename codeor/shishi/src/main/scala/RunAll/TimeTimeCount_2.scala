package RunAll

import com.google.gson.{Gson, JsonParser}
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.kafka.sink.{KafkaRecordSerializationSchema, KafkaSink}
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

import java.text.SimpleDateFormat
import java.util.{Date, Random}

object TimeTimeCount_2 {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        val ks = KafkaSource.builder()
                .setBootstrapServers("192.168.45.10:9092")
                .setTopics("ods_mall_log")
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setGroupId("kfffff")
                .build()

        val ksink = KafkaSink.builder()
                .setBootstrapServers("192.168.45.10:9092")
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                .setValueSerializationSchema(new SimpleStringSchema())
                .setKeySerializationSchema(new SimpleStringSchema())
                .setTopic("log_product_browse").build())
                .build()

        env.fromSource(ks,WatermarkStrategy.noWatermarks(),"awfawf")
                .filter(f => f.contains("product_browse"))
                .map(f =>{
                    val rand = new Random()
                    val log_id = rand.nextInt(10) + new SimpleDateFormat("MMddHHmmssSSS").format(new Date(System.currentTimeMillis()))

                    val strs = f.split("product_browse:\\(")(1).replaceAll("[');]", "").split("\\|")
                    val obj = tableInfo(log_id.toLong, strs(0), strs(1).toInt, strs(2).toInt, strs(3), strs(4))

                    val gson = new Gson()
                    gson.toJson(obj)
                }).sinkTo(ksink)




        env.execute()
    }

    case class tableInfo(log_id : Long,
                         product_id : String,
                         customer_id : Int,
                         gen_order:Int,
                         order_sn : String,
                         modified_time : String)
}
