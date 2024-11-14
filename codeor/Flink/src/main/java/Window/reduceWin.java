package Window;

import WaterSensors.WaterSensor;
import functions.WaterSensorMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

public class reduceWin
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

        SingleOutputStreamOperator<WaterSensor> reduce = window.reduce(new ReduceFunction<WaterSensor>()
        {
            /**
             * 对数据进行聚合
             * @param waterSensor 先前的数据
             * @param t1 传入的数据
             * @return 计算后的数据
             * @throws Exception e
             */
            @Override
            public WaterSensor reduce(WaterSensor waterSensor, WaterSensor t1) throws Exception
            {
                return new WaterSensor(waterSensor.getId(), t1.getTs() + waterSensor.getTs(), t1.getVc() + waterSensor.getVc());
            }
        });

        reduce.print();

        Env.execute();

    }
}
