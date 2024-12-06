package Test02

import com.google.gson.{Gson, JsonParser}
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
import org.apache.flink.streaming.connectors.redis.common.config.{FlinkJedisConfigBase, FlinkJedisPoolConfig}
import org.apache.flink.streaming.connectors.redis.common.mapper.{RedisCommand, RedisCommandDescription, RedisMapper}
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.util.Collector

import scala.math.BigDecimal.RoundingMode

object Tobj21 {
    //TODO 使用Flink 消费kafka 中fact_order_detail 主题的数据，
    //TODO 统计商城每分钟的GMV（结果四舍五入保留两位小数），
    //TODO 将结果存入redis 中（value 为字符串格式，仅存GMV），
    //TODO key 为store_gmv，
    //TODO 使用redis cli 以get key 方式获取store_gmv 值，
    //TODO 将每次截图粘贴至客户端桌面【Release\模块C 提交结果.docx】中对应的任务序号下
    //TODO （每分钟查询一次，至少查询3 次）。
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        val envt = StreamTableEnvironment.create(env)
        val ks = KafkaSource.builder()
                .setBootstrapServers("192.168.45.13:9092")
                .setTopics("fact_order_detail")
                .setGroupId("awff")
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build()

        val r = new FlinkJedisPoolConfig.Builder
        r.setHost("192.168.45.13")
        r.setPort(6379)

        env.fromSource(ks,WatermarkStrategy.noWatermarks(),"kafkasou")
                .keyBy(_.equals(""))
                .window(TumblingProcessingTimeWindows.of(Time.seconds(60)))
                .process(new ProcessWindowFunction[String,Double,Boolean,TimeWindow] {
                    override def process(key: Boolean, context: Context, elements: Iterable[String], out: Collector[Double]): Unit = {
                    //product_cnt
                    //product_price
                        var allmoney = 0D
                        elements.foreach(f =>{
                            val json = JsonParser.parseString(f).getAsJsonObject
                            allmoney = allmoney + (json.get("product_cnt").getAsDouble * json.get("product_price").getAsDouble)
                        })
                        out.collect(allmoney)
                    }
                })addSink(new RedisSink[Double](r.build(),new MyRedisMap))


        env.execute()

    }

    private case class MyRedisMap() extends RedisMapper[Double] {
        override def getCommandDescription: RedisCommandDescription = {
            new RedisCommandDescription(RedisCommand.SET)
        }

        override def getKeyFromData(t: Double): String = {
            "store_gmv"
        }

        override def getValueFromData(t: Double): String = {
            BigDecimal.apply(t).setScale(2,RoundingMode.HALF_UP).toString()
        }
    }
}
