import com.google.gson.Gson
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.kafka.sink.{KafkaRecordSerializationSchema, KafkaSink}
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.planner.expressions.DateFormat

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date
import scala.util.Random

object 实时清洗4_2 {
        def main(args: Array[String]): Unit = {
                System.setProperty("HADOOP_USER_NAME","root")

                val env = StreamExecutionEnvironment.getExecutionEnvironment

                val kafkaSource = KafkaSource.builder()
                        .setBootstrapServers("192.168.45.13:9092")
                        .setTopics("ods_mall_log")
                        .setGroupId("kafka777")
                        .setValueOnlyDeserializer(new SimpleStringSchema())
                        .build()

                val kafkaSink = KafkaSink.builder()
                        .setBootstrapServers("192.168.45.13:9092")
                        .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                                .setTopic("dim_customer_login_log")
                                .setValueSerializationSchema(new SimpleStringSchema())
                                .setKeySerializationSchema(new SimpleStringSchema())
                                .build())

                env.fromSource(kafkaSource,WatermarkStrategy.noWatermarks(),"kafka")
                        .filter(f => {
                                f.contains("customer_login_log")})
                        .map(f =>{
                                val strings = f.replaceAll("customer_login_log:", "")/*.replaceAll("数据开始发送","")*/.replaceAll("[();']", "").split("\\|")
                                //获取时间
                                val ram = new Random()
                                val i = ram.nextInt(10)
                                //9789 | 6749 | 1 | 2024112870294178 |'20241121175450')
                                //customer_login_log  19 | '20241123064249 '| '211.56 .157 .218 '| 1 )
                                val format = new SimpleDateFormat("MMddHHmmssSSS");
                                val data = i + format.format(new Date(System.currentTimeMillis()))

                                val gson = new Gson()
                                gson.toJson(new dataE(
                                        data,
                                        strings(0).toInt,
                                        strings(1),
                                        strings(2),
                                        strings(3).toInt))
                        })
                        .sinkTo(kafkaSink.build())

                env.execute();
        }

        case class dataE(log_id : String,
                        customer_id : Int,
                         login_time : String,
                         order_sn : String,
                         login_type : Int)


}
