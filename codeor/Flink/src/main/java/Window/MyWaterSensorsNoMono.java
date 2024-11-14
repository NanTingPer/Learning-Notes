package Window;

import WaterSensors.WaterSensor;
import functions.MyWatermarkStrategy;
import functions.WaterSensorMapFunction;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkGenerator;
import org.apache.flink.api.common.eventtime.WatermarkGeneratorSupplier;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.time.Duration;
import java.util.Iterator;

public class MyWaterSensorsNoMono
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();
        //并行度
        Env.setParallelism(1);


        SingleOutputStreamOperator<WaterSensor> socketData =
            Env.socketTextStream("192.168.45.13", 7777)
            .map(new WaterSensorMapFunction());

        //自定义水位线
        //创建水位线策略
        WatermarkStrategy<WaterSensor> e = WatermarkStrategy
            //指定水位线策略
            .forGenerator(new WatermarkGeneratorSupplier<WaterSensor>() {
                @Override
                public WatermarkGenerator<WaterSensor> createWatermarkGenerator(Context context)
                {
                    return new MyWatermarkStrategy<>();
                }
            })
            //设定时间戳获取方法
            .withTimestampAssigner(
                new SerializableTimestampAssigner<WaterSensor>()
                {
                    @Override
                    public long extractTimestamp(WaterSensor waterSensor, long l)
                    {
                        System.out.println(waterSensor);
                        //使用Ts作为时间戳 单位是ms毫秒
                        return waterSensor.getTs() * 1000L;
                    }
                }
            );

        //应用水位线策略
        SingleOutputStreamOperator<WaterSensor> TimeWater = socketData.assignTimestampsAndWatermarks(e);


        TimeWater.keyBy(f -> f.getId())
        //使用事件事件
            .window(TumblingEventTimeWindows.of(Time.seconds(10))).process(
            new ProcessWindowFunction<WaterSensor, String, String, TimeWindow>() {
                @Override
                public void process(String s, ProcessWindowFunction<WaterSensor, String, String, TimeWindow>.Context context, Iterable<WaterSensor> iterable, Collector<String> collector) throws Exception
                {
                    //context.window可以获取窗口上下文
                    long startTime = context.window().getStart();
                    long endTime = context.window().getEnd();
                    //时间转换
                    String start = DateFormatUtils.format(startTime, "HH:mm:ss");
                    String end = DateFormatUtils.format(endTime, "HH:mm:ss");

                    System.out.println("===================窗口起始:" + start + " ===================");
                    System.out.println("===================窗口Key:" + s + " 内容===================");
                    Iterator<WaterSensor> iterator = iterable.iterator();
                    while (iterator.hasNext()){
                        System.out.println(iterator.next());
                    }
                    System.out.println("===================窗口结束:" + end + " ===================");
                }
            }
        ).print();

        Env.execute();

    }
}
