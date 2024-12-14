package _20241206

import com.google.gson.JsonParser
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.streaming.connectors.redis.RedisSink
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig
import org.apache.flink.streaming.connectors.redis.common.mapper.{RedisCommand, RedisCommandDescription, RedisMapper}
import org.apache.flink.util.Collector

import scala.math.BigDecimal.RoundingMode

object Tob302 {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        val ks = KafkaSource.builder()
                .setBootstrapServers("192.168.45.13:9092")
                .setTopics("fact_order_detail")
                .setGroupId("awff")
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build()
        val config = new FlinkJedisPoolConfig.Builder
        config.setHost("192.168.45.13")
                .setPort(6379)

        env.fromSource(ks,WatermarkStrategy.noWatermarks(),"ks")
                .keyBy(_.equals(""))
                .window(TumblingProcessingTimeWindows.of(Time.seconds(60)))
                .process(new ProcessWindowFunction[String,String,Boolean,TimeWindow] {
                    override def process(key: Boolean, context: Context, elements: Iterable[String], out: Collector[String]): Unit = {
                        var moneyall = 0d
                        elements.foreach(f =>{
                            val jo = JsonParser.parseString(f).getAsJsonObject
                            val e = jo.get("product_cnt").getAsDouble
                            val money = jo.get("product_price").getAsDouble
                            moneyall = moneyall + (e * money)
                        })
                        out.collect(BigDecimal.apply(moneyall).setScale(2,RoundingMode.HALF_UP).toString())
                    }
                }).addSink(new RedisSink[String](config.build(),new MyRedisMapper))

        env.execute()
    }

    class MyRedisMapper extends RedisMapper[String] {
        override def getCommandDescription: RedisCommandDescription = {
            new RedisCommandDescription(RedisCommand.SET)
        }

        override def getKeyFromData(t: String): String = {
            "store_gmv"
        }

        override def getValueFromData(t: String): String = {
            t
        }
    }

}
