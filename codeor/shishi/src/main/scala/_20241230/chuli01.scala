package _20241230

import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

object chuli01 {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment

        KafkaSource
                .builder()
                .setBootstrapServers("192.168.45.20:9092")
                .setTopics("")


    }

}
