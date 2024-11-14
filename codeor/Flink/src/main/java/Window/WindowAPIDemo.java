package Window;

import WaterSensors.WaterSensor;
import functions.WaterSensorMapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.*;
import org.apache.flink.streaming.api.windowing.time.Time;

public class WindowAPIDemo
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        SingleOutputStreamOperator<WaterSensor> socketData = Env.socketTextStream("192.168.45.13", 7777)
                                                         .map(new WaterSensorMapFunction());

        KeyedStream<WaterSensor, String> Keyby = socketData.keyBy(f -> f.getId());

        //会话窗口 超时时间为10秒
//        socketData.windowAll(ProcessingTimeSessionWindows.withGap(Time.seconds(10)));

        //滚动窗口 窗口长度10秒
//        socketData.windowAll(TumblingProcessingTimeWindows.of(Time.seconds(10)));

        //滑动窗口 窗口长度10秒，间距5秒会出一个新的窗口
//        socketData.windowAll(SlidingProcessingTimeWindows.of(Time.seconds(10),Time.seconds(5)));


        //计数窗口与计数滑动窗口
//        socketData.countWindowAll(10);
//        socketData.countWindowAll(10,5);

        //全局窗口 需要自定义
//        socketData.windowAll(GlobalWindows.create());

        Env.execute();
    }
}
