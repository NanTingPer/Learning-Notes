package OldJobTest

import com.google.gson.{Gson, JsonParser}
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.functions.AggregateFunction
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.function.WindowFunction
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.streaming.connectors.redis.RedisSink
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig
import org.apache.flink.streaming.connectors.redis.common.mapper.{RedisCommand, RedisCommandDescription, RedisMapper}
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.util.Collector

object 实时计算2Ower {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")

        val conf = new FlinkJedisPoolConfig.Builder()
            .setHost("192.168.45.13")   // 记得改为自己的IP地址
            .setPort(6379)
            .build()

        val env = StreamExecutionEnvironment.getExecutionEnvironment;
        val envtabe = StreamTableEnvironment.create(env)

        val data = env.fromSource(KafkaSource.builder()
                .setTopics("fact_order_detail")
                .setBootstrapServers("192.168.45.13:9092")
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setGroupId("awfawfg")
                .build(),
                WatermarkStrategy.noWatermarks(),
                "kafkasr").filter(f => f.contains("已付款"))
            .keyBy(_.contains("已付款"))
            .window(TumblingProcessingTimeWindows.of(Time.minutes(1)))
            .aggregate(new MyAggregateFunction, new MyWindowFunction)
            .map(f => {
                BigDecimal(f).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
            })
//        new RedisSink[Double](conf,new MyRedis)
        data.addSink(new RedisSink[Double](conf,new MyRedis))
        env.execute()
    }

    class MyAggregateFunction extends AggregateFunction[String,Double,Double] {

        //TODO 初始化
        override def createAccumulator(): Double = {
            0
        }

        //TODO 聚合
        override def add(in: String, acc: Double): Double = {
            var run = 0.0D;
            run = acc + JsonParser.parseString(in).getAsJsonObject.get("payment_money").getAsDouble
            run
        }

        //TODO
        override def getResult(acc: Double): Double = {
            acc;
        }


        //TODO 多线程合并
        override def merge(acc: Double, acc1: Double): Double = {
            acc + acc
        }
    }

    class MyWindowFunction extends WindowFunction[Double,Double,Boolean,TimeWindow] {
        override def apply(key: Boolean, window: TimeWindow, input: Iterable[Double], out: Collector[Double]): Unit = {
            var unm = 0.0D
            input.foreach(f => unm += f)
            out.collect(unm)
        }
    }

    class MyRedis extends RedisMapper[Double] {

        //TODO 指定数据结构
        override def getCommandDescription: RedisCommandDescription = {
            new RedisCommandDescription(RedisCommand.SET)
        }

        //TODO 指定Key
        override def getKeyFromData(data: Double): String = {
            "store_gmv"
        }

        //TODO 返回数据
        override def getValueFromData(data: Double): String = {
            data.toString
        }
    }

}
