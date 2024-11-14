package Window;

import WaterSensors.WaterSensor;
import functions.WaterSensorMapFunction;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.Iterator;

public class processWin
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        SingleOutputStreamOperator<WaterSensor> socketData =
            Env.socketTextStream("192.168.45.13", 7777)
            .map(new WaterSensorMapFunction());


        KeyedStream<WaterSensor, String> KeyData = socketData.keyBy(f -> f.getId());

        KeyData.window(TumblingProcessingTimeWindows.of(Time.seconds(10))).process(
            /**
             * WaterSensor 输入的类型
             * String 输出的类型
             * String Key的类型
             * TimeWindow 窗口的类型
             */
            new ProcessWindowFunction<WaterSensor, String, String, TimeWindow>() {

                /**
                 * 只有在窗口到时的时候才会触发一次
                 * @param s Key
                 * @param context 上下文
                 * @param iterable 窗口内的所有数据
                 * @param collector 发送器
                 * @throws Exception e
                 */
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
