package Window;

import WaterSensors.WaterSensor;
import functions.WaterSensorMapFunction;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

public class AggWin
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();
        SingleOutputStreamOperator<WaterSensor> sockData =
            Env.socketTextStream("192.168.45.13", 7777)
               .map(new WaterSensorMapFunction());
        WindowedStream<WaterSensor, String, TimeWindow> window =
            //滚动窗口 10秒
            sockData.keyBy(f -> f.getId())
                    .window(TumblingProcessingTimeWindows.of(Time.seconds(10)));

        SingleOutputStreamOperator<String> aggregate = window.aggregate(new AggregateFunction<WaterSensor, Integer, String>()
        {

            /**
             * 初始化累加器
             * @return 初始值
             */
            @Override
            public Integer createAccumulator()
            {
                System.out.println("初始化器被调用了");
                return 0;
            }

            /**
             * 聚合逻辑
             * @param waterSensor 传入值
             * @param integer 累加器的值
             * @return 聚合后的值
             */
            @Override
            public Integer add(WaterSensor waterSensor, Integer integer)
            {
                System.out.println("聚合被调用了");
                return waterSensor.getVc() + integer;
            }

            /**
             * 最终返回值
             * @param integer 累加器的值
             * @return 最终值
             */
            @Override
            public String getResult(Integer integer)
            {
                System.out.println("值被获取了");
                return integer.toString();
            }

            /**
             * 会话窗口才会用到的
             * @param integer
             * @param acc1
             * @return
             */
            @Override
            public Integer merge(Integer integer, Integer acc1)
            {
                return 0;
            }
        });

        aggregate.print();

        Env.execute();
    }
}
