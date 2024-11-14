package Window;

import WaterSensors.WaterSensor;
import functions.WaterSensorMapFunction;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.ProcessingTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class AggManyWin
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        SingleOutputStreamOperator<WaterSensor> socketData =
            Env.socketTextStream("192.168.45.13", 7777)
               .map(new WaterSensorMapFunction());

        socketData.keyBy(f->f.getId())
                  .window(ProcessingTimeSessionWindows
                  .withGap(Time.seconds(10)))
                  .aggregate(new MyAggFunction(),new MyProcessFunction())
                  .print();

        Env.execute();
    }

    /**
     * 1,传入的值的类型<br>
     * 2,过程钟值的类型<br>
     * 3,最终返回的类型<br>
     */
    public static class MyAggFunction implements AggregateFunction<WaterSensor,Integer,String>{

        public Integer createAccumulator()
        {
            System.out.println("初始化聚合值");
            return 0;
        }

        public Integer add(WaterSensor waterSensor, Integer integer)
        {
            System.out.println("触发聚合逻辑");
            //返回总vc值
            return integer + waterSensor.getVc();
        }

        public String getResult(Integer integer)
        {
            System.out.println("取得聚合结果");
            return integer.toString();
        }

        public Integer merge(Integer integer, Integer acc1)
        {
            return 0;
        }
    }


    /**
     * 由于是双函数调用<br>
     * 这个函数最终被传入的值是Agg的返回值<br>
     *<br>
     * 1，输入类型<br>
     * 2，输出类型<br>
     * 3，Key 类型<br>
     * 4，窗口类型<br>
     */
    public static class MyProcessFunction extends ProcessWindowFunction<String,String,String, TimeWindow>{

        public void process(String s, ProcessWindowFunction<String, String, String, TimeWindow>.Context context, Iterable<String> iterable, Collector<String> collector) throws Exception
        {
            //获取上下文 获取窗口的起始结束时间
            TimeWindow window = context.window();
            String startTime = DateFormatUtils.format(window.getStart(), "HH:mm:ss");
            String endTime = DateFormatUtils.format(window.getEnd(), "HH:mm:ss");

            System.out.println("窗口"+s+"开始时间: " + startTime);
            System.out.println("窗口"+s+"结束时间: " + endTime);
            System.out.println("窗口"+s+"最终结果: " + iterable.toString());

            collector.collect(iterable.toString() + "是Process哦");
        }
    }
}
