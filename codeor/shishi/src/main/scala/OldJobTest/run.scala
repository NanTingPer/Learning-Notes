package OldJobTest

import com.google.gson.JsonParser
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.functions.AggregateFunction
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.streaming.connectors.redis.RedisSink
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig
import org.apache.flink.streaming.connectors.redis.common.mapper.{RedisCommand, RedisCommandDescription, RedisMapper}
import org.apache.flink.util.Collector

/**
 *
 * fact_order_master Topic中数据的JSON格式如下：
 * {
 *  "order_id":6097,
 *  "order_sn":"2022111417659492",
 *  "customer_id":583,
 *  "shipping_user":"龚桂芳",
 *  "province":"上海市",
 *  "city":"上海市",
 *  "address":"上海市上海市真光路12889755号3层",
 *  "order_source":1,
 *  "payment_method":3,
 *  "order_money":4125.78,
 *  "district_money":0.00,
 *  "shipping_money":39.06,
 *  "payment_money":4164.84,
 *  "shipping_comp_name":"韵达",
 *  "shipping_sn":"9846757521358",
 *  "create_time":"20221110023759",
 *  "shipping_time":"20221110132159",
 *  "pay_time":"20221110032759",
 *  "receive_time":"20221112203359",
 *  "order_status":"已签收",
 *  "order_point":416,
 *  "invoice_title":"雨林木风计算机传媒有限公司",
 *  "modified_time":"2022-11-12 12:33:59"
 * }
 *
 */

object run {

    // 输入订单事件类型（只取订单状态和支付金额两个字段）
    case class OrderEvent(order_status:String, payment_money: Double)

    // 主方法
    def main(args: Array[String]) {
        // 设置流执行环境
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        env.setParallelism(1)   // 设置并行度，便于观察

        // kafka source
        val source = KafkaSource.builder[String]
            .setBootstrapServers("192.168.190.139:9092")       // 记得改为自己的IP地址
            .setTopics("fact_order_master")
            .setGroupId("group-test")
            .setStartingOffsets(OffsetsInitializer.earliest)
            .setValueOnlyDeserializer(new SimpleStringSchema)
            .build

        // 定义redis sink的配置 (默认端口号6379)
        val conf = new FlinkJedisPoolConfig.Builder()
            .setMaxTotal(1000)
            .setMaxIdle(32)
            .setTimeout(10*1000)
            .setHost("192.168.190.139")   // 记得改为自己的IP地址
            .setPort(6379)
            .build()

        // 流处理管道
        val orderStream = env
            // 指定Kafka数据源
            .fromSource(source, WatermarkStrategy.noWatermarks[String], "Kafka Source")
            // 转换为OrderEvent对象
            .map(line => {
                val jsonObj = JsonParser.parseString(line).getAsJsonObject
                val order_status = jsonObj.getAsJsonObject("order_status").toString    // 订单状态
                val payment_money = jsonObj.getAsJsonObject("payment_money").getAsDouble  // 支付金额
                OrderEvent(order_status, payment_money)
            })
            // 过滤出"已付款"的订单项
            .filter(_.order_status=="已付款")
            // 分区
            .keyBy(_.order_status)
            // 指定窗口
            .window(TumblingEventTimeWindows.of(Time.minutes(1)))
            // 执行窗口聚合函数
            .aggregate(new AggAvgTemp, new ProcessAvgTemp)

        // orderStream.print()

        // Redis Sink
        orderStream.addSink(new RedisSink[Double](conf, new RedisSinkMapper))

        // execute program
        env.execute("Flink Streaming Task03")
    }

    // 窗口增量聚合函数
    class AggAvgTemp extends AggregateFunction[OrderEvent, Double, Double] {
        // 创建初始ACC
        override def createAccumulator = 0.0

        // 累加每个订单的支付金额
        override def add(input: OrderEvent, acc: Double) = {
            acc + input.payment_money
        }

        // 分区合并
        override def merge(acc1: Double, acc2: Double) = {
            acc1 + acc2
        }

        // 返回已下单订单的总支付金额
        override def getResult(acc: Double): Double = acc
    }

    // 窗口处理函数
    class ProcessAvgTemp extends ProcessWindowFunction[Double, Double, String, TimeWindow] {
        override def process(id: String,
                             context: Context,
                             events: Iterable[Double],
                             out: Collector[Double]): Unit = {
            // 注意，Iterable[Double]将只包含一个读数,
            // 即MyReduceFunction计算出的预先聚合的平均值。
            val total_pay_money = events.iterator.next
            out.collect(total_pay_money)
        }
    }

    // redisMap接口，设置key和value
    // Redis Sink 核心类是 RedisMappe 接口，使用时要编写自己的redis操作类实现这个接口中的三个方法
    class RedisSinkMapper extends RedisMapper[Double] {
        // getCommandDescription：设置数据使用的数据结构 HashSet 并设置key的名称
        override def getCommandDescription: RedisCommandDescription = {
            // RedisCommand.HSET 指定存储类型
            new RedisCommandDescription(RedisCommand.SET)
        }

        /**
         * 获取 value值 value的数据是键值对
         *
         * @param data
         * @return
         */
        //指定key
        // 查看所有key：keys *             查看指定key：get top3itemamount
        override def getKeyFromData(event: Double): String = "store_gmv"

        // 指定value
        override def getValueFromData(event: Double): String = event.toString
    }
}