package _20250218

import com.google.gson.{Gson, JsonParser}
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.functions.MapFunction
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.base.DeliveryGuarantee
import org.apache.flink.connector.kafka.sink.{KafkaRecordSerializationSchema, KafkaSink}
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.{Date, Random}
object 清洗02 {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment

        val ks = KafkaSource
                .builder()
                .setBootstrapServers("192.168.45.13:9092")
                .setTopics("ods_mall_log")
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setGroupId("qinxi02")
                .build()

        val sinkSchema = KafkaRecordSerializationSchema
                .builder()
                .setValueSerializationSchema(new SimpleStringSchema())
                .setKeySerializationSchema(new SimpleStringSchema())
                .setTopic("dwd.dim_customer_login_log")
                .build()

        val kk = KafkaSink
                .builder()
                .setBootstrapServers("192.168.45.13:9092")
                .setRecordSerializer(sinkSchema)
                .build()


        //Data Handle
        //   customer_login_log:(8463 |'20250213090514'|'48.69.243.189'|0);
        val map = new MapFunction[String,String] {
            override def map(t: String): String = {
                val value = t.split("customer_login_log:")(1).replaceAll("['() ]","")
                val values = value.split("\\|")
                val randmo = new Random().nextInt(10) + new SimpleDateFormat("MMddHHmmssSSS").format(new Date(System.currentTimeMillis()))
                val gson = new Gson()
                gson.toJson(table(randmo.toLong, values(0), values(1).toInt, values(2).toInt, values(3), values(4)))
            }
        }

        env
                .fromSource(ks, WatermarkStrategy.noWatermarks(), "kafkas")
                .filter(s => s.contains("customer_login_log"))
                .map(map)
                .sinkTo(kk)

        env.execute()
    }

    case class table(log_id : Long,
                     product_id : String,
                     customer_id : Int,
                     gen_order : Int,
                     order_sn : String,
                     modified_time : String)

}
