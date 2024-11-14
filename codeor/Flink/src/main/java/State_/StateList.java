package State_;

import WaterSensors.WaterSensor;
import functions.WaterSensorMapFunction;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.util.ArrayList;

public class StateList
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        Env.socketTextStream("192.168.45.13",7777)
               .map(new WaterSensorMapFunction())
                   .assignTimestampsAndWatermarks(WatermarkStrategy.<WaterSensor>forMonotonousTimestamps()
                       .withTimestampAssigner(new SerializableTimestampAssigner<WaterSensor>() {
                           public long extractTimestamp(WaterSensor waterSensor, long l)
                           {
                               return waterSensor.getTs() * 1000L;
                           }
                       }))
                //TODO 按照键分组
               .keyBy(f -> f.getId())
               .process(new KeyedProcessFunction<String, WaterSensor, String>() {
                   @Override
                   public void open(Configuration parameters) throws Exception
                   {
                       //TODO 在Open方法初始化 只会初始化一次，能保证任务启动后初始化
                       super.open(parameters);
                       state = getRuntimeContext().getListState(new ListStateDescriptor<>("dawf", Types.INT));
                   }

                   private ListState<Integer> state;
                   public void processElement(WaterSensor value, KeyedProcessFunction<String, WaterSensor, String>.Context ctx, Collector<String> out) throws Exception
                   {
                       //TODO 其返回的是可迭代类型
                       Iterable<Integer> integers = state.get();
                       //TODO 创建一个List用来存放值
                       ArrayList<Integer> list = new ArrayList<>();
                       for (Integer integer : integers)
                       {
                           list.add(integer);
                       }
                       list.add(value.getVc());
                       //TODO 排序
                       list.sort((o1,o2) -> o2 - o1);
                       //TODO 只取前三
                       if(list.size() > 3) list.remove(3);
                       //TODO 返回
                       out.collect("Key:" + value.getId() + "\t" + list.toString());

                       //TODO 覆盖
                       state.update(list);
                       list.clear();
                   }
               }).print();

        Env.execute();
    }
}
