package State_;

import WaterSensors.WaterSensor;
import functions.WaterSensorMapFunction;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

public class StateTTL
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        Env.socketTextStream("192.168.45.13",7777)
               .map(new WaterSensorMapFunction())
                   .assignTimestampsAndWatermarks(WatermarkStrategy.<WaterSensor>forMonotonousTimestamps()
                       .withTimestampAssigner((waterSensor, l) -> waterSensor.getTs() * 1000L))
               .keyBy(f -> f.getId())
               .process(new KeyedProcessFunction<String, WaterSensor, String>() {
                   ValueState<Integer> valueState;

                   @Override
                   public void open(Configuration parameters) throws Exception
                   {
                       StateTtlConfig.Builder TTLConfig =
                                StateTtlConfig
                               //TODO 创建TTL 传入过期时间
                               .newBuilder(Time.seconds(10))
                               //TODO 选择更新状态的模式
                               //TODO 在给定情况下，状态的TTL会被重置
                               .setUpdateType(StateTtlConfig.UpdateType.OnReadAndWrite)
                               //TODO 选择被标记清除的状态是否能被读取
                               //TODO 默认是这个 不能，另一个是可以 - ReturnExpiredIfNotCleanedUp
                               .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired);

                       ValueStateDescriptor<Integer> VSD =  new ValueStateDescriptor<>("ValueTTL",Types.INT);
                       //TODO 启用TTL 并给定TTL配置
                       VSD.enableTimeToLive(TTLConfig.build());
                       valueState = getRuntimeContext().getState(VSD);
                       super.open(parameters);
                   }

                   @Override
                   public void processElement(WaterSensor value, KeyedProcessFunction<String, WaterSensor, String>.Context ctx, Collector<String> out) throws Exception
                   {
                       System.out.println("Key:" + value.getId() + "\t上一个水位:" + valueState.value());
                       valueState.update(value.getVc());
                   }
               }).print();

        Env.execute();
    }
}
