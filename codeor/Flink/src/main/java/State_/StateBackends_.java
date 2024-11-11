package State_;

import WaterSensors.WaterSensor;
import functions.WaterSensorMapFunction;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StateBackends_
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        Env.setStateBackend(new HashMapStateBackend());
        //TODO 可以传入一个true表示启用增量保存 不指定就是全量
        Env.setStateBackend(new EmbeddedRocksDBStateBackend());

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
                //TODO 目标
                //TODO 统计每种水位出现的次数
               .process(new KeyedProcessFunction<String, WaterSensor, String>() {
                   //TODO 这里不初始化
                   //TODO 键是水位 值是次数
                   MapState<Integer,Integer> mapState;

                   public void open(Configuration parameters) throws Exception
                   {
                       super.open(parameters);
                       //TODO 在open初始化
                       mapState = getRuntimeContext().getMapState(new MapStateDescriptor<>("mapsta",Types.INT,Types.INT));
                   }

                   public void processElement(WaterSensor value, KeyedProcessFunction<String, WaterSensor, String>.Context ctx, Collector<String> out) throws Exception
                   {
                       int vc = value.getVc();
                       //TODO 返回true存在
                        if(mapState.contains(value.getVc())) mapState.put(vc, mapState.get(vc) + 1);
                        //TODO 不存在
                        if(!mapState.contains(value.getVc())) mapState.put(vc, 1);

                        //TODO 遍历拼接返回
                       Iterator<Map.Entry<Integer, Integer>> iterator = mapState.iterator();
                       StringBuilder stb = new StringBuilder();
                       stb.append(value.getId() + "\n===========================\n");
//                       while (iterator.hasNext())
//                       {
//                           Map.Entry<Integer, Integer> next = iterator.next();
//                           stb.append(next.getKey() + "\t" + next.getValue() + "\n");
//                       }

                       //TODO 以水位线大小排序
                       List<Tuple2<Integer,Integer>> list = new ArrayList<>();
                       while (iterator.hasNext())
                       {
                           Map.Entry<Integer, Integer> next = iterator.next();
                           list.add(new Tuple2<>(next.getKey(),next.getValue()));
                       }
                       list.sort((o1,o2) -> o1.f0 - o2.f0);
                       for (Tuple2<Integer, Integer> e : list)
                       {
                           stb.append(e.f0 + "\t" + e.f1 + "\n");
                       }

                       out.collect(stb.toString());
                   }
               }).print();

        Env.execute();
    }
}
