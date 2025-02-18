package _20250218

import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

object 计算01 {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        val envt = StreamTableEnvironment.create(env)

        val ks = KafkaSource
                .builder()
                .setBootstrapServers("192.168.45.13:9092")
                .setGroupId("hhh")
                .setTopics("log_product_browse")
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build()

        env
                .fromSource(ks, WatermarkStrategy.noWatermarks(), "ksad")

    }
}
