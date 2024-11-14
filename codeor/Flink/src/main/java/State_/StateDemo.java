package State_;

import WaterSensors.WaterSensor;
import functions.WaterSensorMapFunction;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.client.program.StreamContextEnvironment;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

public class StateDemo
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamContextEnvironment.getExecutionEnvironment();

        Env.socketTextStream("192.168.45.13",7777)
               .map(new WaterSensorMapFunction())
               .assignTimestampsAndWatermarks(WatermarkStrategy.<WaterSensor>forMonotonousTimestamps().withTimestampAssigner(new SerializableTimestampAssigner<WaterSensor>() {
                       @Override
                       public long extractTimestamp(WaterSensor waterSensor, long l)
                       {
                           return waterSensor.getTs() * 1000L;
                       }
                   }))
               .keyBy(f -> f.getId())
               .process(new KeyedProcessFunction<String, WaterSensor, String>() {
                   // TODO 定义一个值状态，值状态存储的类型是Integer
                   private ValueState<Integer> valueState;

                   // TODO 开始时触发
                   @Override
                   public void open(Configuration parameters) throws Exception
                   {
                       super.open(parameters);
                       //TODO 使用运行时上下文创建一个值状态,在Open里面初始化valueState
                       //TODO 因为可以避免重复初始化 也可以避免任务没有启动就已经初始化导致出错
                       valueState = getRuntimeContext().getState(new ValueStateDescriptor<>("value", Types.INT));
                   }

                   // TODO 逻辑
                   @Override
                   public void processElement(WaterSensor value, KeyedProcessFunction<String, WaterSensor, String>.Context ctx, Collector<String> out) throws Exception
                   {
                        //TODO 因为使用的Integer包装类，所以默认值为null
                        int Vc = valueState.value() == null ? 0 : valueState.value();
                        //TODO 如果大于等于10就报警
                        if((Math.abs(Vc - value.getVc())>=10))
                            out.collect("传感器" + value.getId() + "\t" + Vc + "\t" + value.getVc()+"\t"+"报警！！！");
                        valueState.update(value.getVc());
                   }
               }).print();

        Env.execute();
    }
}
