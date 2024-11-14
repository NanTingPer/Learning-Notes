package Process_;

import WaterSensors.WaterSensor;
import functions.WaterSensorMapFunction;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.TimerService;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

public class KeyProcess
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        Env.setParallelism(1);

        SingleOutputStreamOperator<WaterSensor> socketData = Env
            .socketTextStream("192.168.45.13", 7777)
            .map(new WaterSensorMapFunction())
            //设置水位线
            .assignTimestampsAndWatermarks(WatermarkStrategy
                .<WaterSensor>forMonotonousTimestamps()
                //使用TS为水位线
                .withTimestampAssigner(new SerializableTimestampAssigner<WaterSensor>() {
                    @Override
                    public long extractTimestamp(WaterSensor waterSensor, long l)
                    {
                        return waterSensor.getTs() * 1000L;
                    }
                })
            );

        //ID为Key分组
        socketData.keyBy(f -> f.getId())
                  /**
                   * 第一个类型是Key的类型,第二个类型是数据的类型,第三个是输出的类型
                   */
                  // TODO 使用处理函数
                      .process(new KeyedProcessFunction<String, WaterSensor, String>() {
                          /**
                           * 来了数据触发
                           * @param waterSensor 来的数据
                           * @param context 上下文
                           * @param collector 数据采集器
                           * @throws Exception e
                           */
                          public void processElement(WaterSensor waterSensor, KeyedProcessFunction<String, WaterSensor, String>.Context context, Collector<String> collector) throws Exception
                          {
                              //注册事件时间器 间隔5秒
//                              ServiceE.registerEventTimeTimer(5000L);
                              //注册处理事件计时器 间隔5秒
                              //使用上下文获取事件器
                              TimerService ServiceE = context.timerService();
                              // TODO ServiceE.currentProcessingTime()获取系统时间 java自己也有
                              // TODO + 5000L表示5秒后触发
                              ServiceE.registerProcessingTimeTimer(ServiceE.currentProcessingTime() + 5000L);
                              System.out.println("处理时间计时器被创建Key\t" + waterSensor.getId() +"\t" + ServiceE.currentProcessingTime());
                          }

                          /**
                           * 事件触发
                           * @param timestamp 触发计时器的时间戳
                           * @param ctx 上下文
                           * @param out 用于返回结果的收集器
                           * @throws Exception e
                           */
                          public void onTimer(long timestamp, KeyedProcessFunction<String, WaterSensor, String>.OnTimerContext ctx, Collector<String> out) throws Exception
                          {
                              // TODO 获取该计时器的Key
                              String currentKey = ctx.getCurrentKey();
                              System.out.println(currentKey + "计时器触发\t" + timestamp);
                              super.onTimer(timestamp, ctx, out);
                          }
                      }).print();

        Env.execute();

    }
}
