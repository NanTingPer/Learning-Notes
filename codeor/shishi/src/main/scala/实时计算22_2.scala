import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.kafka.sink.{KafkaRecordSerializationSchema, KafkaSink}
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.redis.RedisSink
import org.apache.flink.streaming.connectors.redis.common.config.{FlinkJedisClusterConfig, FlinkJedisConfigBase, FlinkJedisPoolConfig, FlinkJedisSentinelConfig}
import org.apache.flink.streaming.connectors.redis.common.mapper.{RedisCommand, RedisCommandDescription, RedisMapper}

object 实时计算22_2 {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        val ks = KafkaSource.builder()
                .setBootstrapServers("192.168.45.10:9092")
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setTopics("fact_order_detail")
                .setGroupId("kafkaddd")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .build()

        val config = new FlinkJedisPoolConfig.Builder
        config.setHost("192.168.45.10")
        config.setPort(6379)

        env.fromSource(ks,WatermarkStrategy.noWatermarks(),"kafkastr")
                .addSink(new RedisSink[String](config.build(),new MyRedisSink))



    }

    class MyRedisSink extends RedisMapper[String] {
        //TODO 数据结构
        override def getCommandDescription: RedisCommandDescription = {
            new RedisCommandDescription(RedisCommand.SET)
        }

        //TODO key
        override def getKeyFromData(t: String): String = {
            "key_r"
        }

        //TODO 取值
        override def getValueFromData(t: String): String = {
            t
        }
    }
}
