package Window;

import WaterSensors.WaterSensor;
import functions.WaterSensorMapFunction;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.ProcessingTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.assigners.SessionWindowTimeGapExtractor;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.Iterator;

public class SessionWin
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();
        SingleOutputStreamOperator<WaterSensor> socketData = Env.socketTextStream("192.168.45.13", 7777)
                                                                .map(new WaterSensorMapFunction());
        KeyedStream<WaterSensor, String> Keyby = socketData.keyBy(f -> f.getId());

        Keyby
            //使用会话窗口
            .window(ProcessingTimeSessionWindows
                //动态会话时常 单位是ms毫秒
            .withDynamicGap(new SessionWindowTimeGapExtractor<WaterSensor>()
            {
                @Override
                public long extract(WaterSensor waterSensor)
                {
                    return waterSensor.getVc() * 1000L;
                }
            }))
            .process(new ProcessWindowFunction<WaterSensor, String, String, TimeWindow>()
                     {
                         public void process(String s, ProcessWindowFunction<WaterSensor, String, String, TimeWindow>.Context context, Iterable<WaterSensor> iterable, Collector<String> collector) throws Exception
                         {
                             //context.window可以获取窗口上下文
                             long startTime = context
                                 .window()
                                 .getStart();
                             long endTime = context
                                 .window()
                                 .getEnd();
                             //时间转换
                             String start = DateFormatUtils.format(startTime, "HH:mm:ss");
                             String end = DateFormatUtils.format(endTime, "HH:mm:ss");

                             System.out.println("===================窗口起始:" + start + " ===================");
                             System.out.println("===================窗口Key:" + s + " 内容===================");
                             Iterator<WaterSensor> iterator = iterable.iterator();
                             while (iterator.hasNext())
                             {
                                 System.out.println(iterator.next());
                             }
                             System.out.println("===================窗口结束:" + end + " ===================");

                         }
                     }
            ).print();

        Env.execute();
    }
}
