package State_;

import WaterSensors.WaterSensor;
import functions.WaterSensorMapFunction;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.ReducingState;
import org.apache.flink.api.common.state.ReducingStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.util.ArrayList;

public class Reducing_
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

               ReducingState<Integer> reducingState;

               @Override
               public void open(Configuration parameters) throws Exception
               {
                   super.open(parameters);
                   reducingState = getRuntimeContext().getReducingState(new ReducingStateDescriptor<>(
                       "Name",
                       //TODO 可以简化为喇嘛大
                       new ReduceFunction<Integer>() {

                           //TODO 直接累加
                           @Override
                           public Integer reduce(Integer integer, Integer t1) throws Exception
                           {
                               return integer + t1;
                           }
                       },
                       Types.INT
                   ));
               }

               @Override
               public void processElement(WaterSensor value, KeyedProcessFunction<String, WaterSensor, String>.Context ctx, Collector<String> out) throws Exception
               {
                   //将水位线传过去
                    reducingState.add(value.getVc());
                    out.collect("Key:" + value.getId() + "\t 水位线: " + reducingState.get());
               }
           }).print();

        Env.execute();
    }
}
