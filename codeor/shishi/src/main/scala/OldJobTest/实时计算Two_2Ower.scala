package OldJobTest

import com.google.gson.JsonParser
import com.ss2023.run.RedisSinkMapper
import org.apache.flink.api.common.eventtime.{WatermarkGenerator, WatermarkGeneratorSupplier, WatermarkOutput, WatermarkStrategy}
import org.apache.flink.api.common.functions.AggregateFunction
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.function.{ProcessWindowFunction, WindowFunction}
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.streaming.connectors.redis.RedisSink
import org.apache.flink.streaming.connectors.redis.common.config.{FlinkJedisConfigBase, FlinkJedisPoolConfig}
import org.apache.flink.streaming.connectors.redis.common.mapper.{RedisCommand, RedisCommandDescription, RedisMapper}
import org.apache.flink.util.Collector

import scala.math.BigDecimal.RoundingMode

object 实时计算Two_2Ower {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val env = StreamExecutionEnvironment.getExecutionEnvironment

        val source = KafkaSource.builder()
            .setTopics("fact_order_detail")
            .setBootstrapServers("192.168.45.13:9092")
            .setValueOnlyDeserializer(new SimpleStringSchema())
            .setGroupId("awf")
            .build()

        var config = new FlinkJedisPoolConfig.Builder
        config.setHost("192.168.45.13")
        config.setPort(6379)
        env.fromSource(source,WatermarkStrategy.noWatermarks(),"waf")
            .keyBy(f => true)
            .window(TumblingProcessingTimeWindows.of(Time.minutes(1)))
            .aggregate(new MyAggFunction,new MyWindowsFunction)
            .addSink(new RedisSink[String](config.build(),new MyRedisSink))
        env.execute()

    }

    class MyWindowsFunction extends WindowFunction[Double,String,Boolean,TimeWindow] {
        override def apply(key: Boolean, window: TimeWindow, input: Iterable[Double], out: Collector[String]): Unit = {
            var double2 = 0d
            input.foreach(f => double2 += f)
            out.collect(BigDecimal.apply(double2).setScale(2,RoundingMode.HALF_UP).toString())
        }
    }

    class MyAggFunction extends AggregateFunction[String,Double,Double] {
        override def createAccumulator(): Double = {
            0d
        }

        override def add(in: String, acc: Double): Double = {
//            var jsonObject = JsonParser.parseString(in).getAsJsonObject
//            var e = jsonObject.get("product_cnt").getAsDouble
//            var b = jsonObject.get("product_price").getAsDouble
//            e * b + acc
            var  c = in.replaceAll("[\"{} ]","").split(",")(4).split(":")(1).toDouble * in.replaceAll("[\"{} ]","").split(",")(5).split(":")(1).toDouble
            acc + c
        }

        override def getResult(acc: Double): Double = {
            acc
        }

        override def merge(acc: Double, acc1: Double): Double = {
            acc + acc1
        }
    }

    class MyRedisSink extends RedisMapper[String] {
        override def getCommandDescription: RedisCommandDescription = {
            new RedisCommandDescription(RedisCommand.SET)
        }

        override def getKeyFromData(data: String): String = {
            "run2"
        }

        override def getValueFromData(data: String): String = {
            data
        }
    }


}
