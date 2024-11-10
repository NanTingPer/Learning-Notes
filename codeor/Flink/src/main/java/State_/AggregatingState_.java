package State_;

import WaterSensors.WaterSensor;
import functions.WaterSensorMapFunction;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.state.AggregatingState;
import org.apache.flink.api.common.state.AggregatingStateDescriptor;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.util.ArrayList;

public class AggregatingState_
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

               //TODO 第一个泛型 输入类型，第二个泛型 输出类型
               AggregatingState<Integer,Integer> aggstate;

               @Override
               public void open(Configuration parameters) throws Exception
               {
                   super.open(parameters);
                   aggstate = getRuntimeContext().getAggregatingState(new AggregatingStateDescriptor<Integer, Tuple2<Integer,Integer>, Integer>(
                       "NameAgg",
                       new AggregateFunction<Integer, Tuple2<Integer,Integer>, Integer>() {

                           //初始化
                           @Override
                           public Tuple2<Integer, Integer> createAccumulator()
                           {
                               return new Tuple2<>(0,0);
                           }

                           //累加逻辑
                           @Override
                           public Tuple2<Integer, Integer> add(Integer Input, Tuple2<Integer, Integer> ACC)
                           {
                               //TODO f0存放总量，f1存放次数
                               return Tuple2.of(ACC.f0 + Input,ACC.f1 + 1);
                           }

                           //最终返回
                           @Override
                           public Integer getResult(Tuple2<Integer, Integer> ACC)
                           {
                               //TODO取平均值
                               return ACC.f0 / ACC.f1;
                           }

                           @Override
                           public Tuple2<Integer, Integer> merge(Tuple2<Integer, Integer> integerIntegerTuple2, Tuple2<Integer, Integer> acc1)
                           {
                               return null;
                           }
                       },
                       Types.TUPLE(Types.INT,Types.INT)
                       ));

               }
               @Override
               public void processElement(WaterSensor value, KeyedProcessFunction<String, WaterSensor, String>.Context ctx, Collector<String> out) throws Exception
               {
                    aggstate.add(value.getVc());
                    out.collect("Key:" + value.getId() + "\t" + "水位均值: " + aggstate.get());
               }
           }).print();

        Env.execute();
    }
}
