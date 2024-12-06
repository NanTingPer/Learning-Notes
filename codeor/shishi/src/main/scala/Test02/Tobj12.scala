package Test02

import com.google.gson.Gson
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.kafka.sink.{KafkaRecordSerializationSchema, KafkaSink}
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

import java.text.SimpleDateFormat
import java.util.{Date, Random}

object Tobj12 {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        val ks = KafkaSource.builder()
                .setBootstrapServers("192.168.45.13:9092")
                .setTopics("ods_mall_log")
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setGroupId("awfg")
                .build()

        val ss = KafkaSink.builder()
                .setBootstrapServers("192.168.45.13:9092")
                .setRecordSerializer(KafkaRecordSerializationSchema
                        .builder()
                .setValueSerializationSchema(new SimpleStringSchema())
                .setKeySerializationSchema(new SimpleStringSchema())
                .setTopic("log_product_browse")
                .build())
                .build()
        env.fromSource(ks,WatermarkStrategy.noWatermarks(),"awf")
                .filter(f => f.contains("product_browse"))
                .map(f =>{
                    val log_id = new Random().nextInt(10) + new SimpleDateFormat("MMddHHmmssSSS").format(new Date(System.currentTimeMillis()))
                    val strs = f.replaceAll("数据发送开始:", "").replaceAll("product_browse:\\(", "").replaceAll("[');]", "").split("\\|")
                    val gson = new Gson()
                    gson.toJson(tableinfo(log_id.toLong ,strs(0),strs(1).toInt,strs(2).toInt,strs(3)))
                }).sinkTo(ss)


        env.execute()
    }

    case class tableinfo(log_id:Long,
                         product_id : String,
                         customer_id:Int,
                         gen_order : Int,
                         modified_time:String)
}
