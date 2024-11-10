package Process_;

import WaterSensors.WaterSensor;
import functions.WaterSensorMapFunction;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.TreeMap;

// TODO 统计10秒内的水位线出现次数前2的，5秒输出一次
// TODO 使用滑动窗口 10,5
public class TopN_
{
    // TODO 窗口事件设置为了10秒 滑动时间设置为了5秒
    // TODO 使用WaterSensor的Ts作为了事件时间

    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        Env.setParallelism(1);

        Env.socketTextStream("192.168.45.13",7777)
               .map(new WaterSensorMapFunction())
               .assignTimestampsAndWatermarks(WatermarkStrategy
               .<WaterSensor>forMonotonousTimestamps()
               .withTimestampAssigner((waterSensor, l) -> waterSensor.getTs() * 1000L))
               .windowAll(SlidingEventTimeWindows.of(Time.seconds(10),Time.seconds(5)))
               .process(new ProcessAllWindowFunction<WaterSensor, String, TimeWindow>() {
                   @Override
                   public void process(ProcessAllWindowFunction<WaterSensor, String, TimeWindow>.Context context, Iterable<WaterSensor> elements, Collector<String> out) throws Exception
                   {
                       TreeMap<Long,Integer> tr = new TreeMap<>();
                       for (WaterSensor element : elements)
                       {
                           //TODO 判断是否为第一条数据
                           // TODO 如果不是第一条数据
                           Long vce = (long) element.vc;
                           if(tr.containsKey(vce))
                           {
                               tr.put(vce,tr.get(vce) + 1);
                           }
                           //TODO 否则就是第一次来
                           else
                           {
                               tr.put(vce,1);
                           }
                       }

                       // TODO 对数据进行排序
                       ArrayList<Tuple2<Long,Integer>> list = new ArrayList<>();

                       //TODO 载入数据
                       tr.forEach((k,v) -> {
                           list.add(new Tuple2<>(k,v));
                       });

                       //TODO 排序数据
                       list.sort((o1,o2) -> o2.f1 - o1.f1);

                       StringBuilder stb = new StringBuilder();

                       for(int i = 0;i < Math.min(list.size(),2);i++){
                           Tuple2<Long, Integer> tp2 = list.get(i);
                           stb.append("第" + i+1 + ": " +tp2.f0 + "\t" + tp2.f1 + "\n" + "================" + "\n");
                       }

                       out.collect(stb.toString());

                   }
               }).print();

        Env.execute();
    }
}
