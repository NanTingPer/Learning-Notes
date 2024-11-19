import com.google.gson.Gson
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.kafka.sink.{KafkaRecordSerializationSchema, KafkaSink}
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

import java.text.SimpleDateFormat
import java.time.{LocalDateTime, ZoneId}
import java.time.format.DateTimeFormatter
import java.util.Date
import scala.util.Random

object 实时清洗2Ower {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime)

        val ser = "192.168.45.13:9092"

        val kafk = KafkaSource.builder()
            .setTopics("ods_mall_log")
            .setBootstrapServers(ser)
            .setGroupId("Kafka7777")
            .setStartingOffsets(OffsetsInitializer.earliest())
            .setValueOnlyDeserializer(new SimpleStringSchema())
            .build()

        val kafkasink = KafkaSink.builder[String]
            .setBootstrapServers(ser)
            .setRecordSerializer(
                KafkaRecordSerializationSchema.builder()
                    .setTopic("dim_customer_login_log")
                    .setValueSerializationSchema(new SimpleStringSchema())
                    .setKeySerializationSchema(new SimpleStringSchema())
                    .build()).build();

        val data = env.fromSource(kafk, WatermarkStrategy.noWatermarks(), "d666")
            .filter(f => f.contains("customer_login_log"))

        data.map(f => {
            val gson = new Gson
            val str = f.replaceAll("customer_login_log:\\(", "").replaceAll("[;)']", "").split("\\|")
            //生成 字段

            val ran = new Random()
            var i = ran.nextInt(10)
            val format = new SimpleDateFormat("MMddHHmmssSSS")
            var time = i + format.format(new Date(System.currentTimeMillis))

            gson.toJson(new format(
                time,
                str(0).toInt,
                str(1),
                str(2),
                str(3).toInt
            ))
        }).sinkTo(kafkasink)

        env.execute()
    }

    case class format(login_id:String,
                      customer_id:Int,
                      login_time:String,
                      login_ip:String,
                      login_type:Int)
}
