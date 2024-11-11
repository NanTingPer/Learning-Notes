package State_;

import WaterSensors.WaterSensor;
import functions.WaterSensorMapFunction;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.*;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

public class OperMapState
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        Env.setParallelism(2);
        Env.socketTextStream("192.168.45.13",7777)
               .map(new MyMapFunction()).print();

        Env.execute();
    }

    public static class MyMapFunction implements MapFunction<String,Long>, CheckpointedFunction{

        public Long map(String s) throws Exception
        {
            return ++conunt;
        }

        Long conunt = 0L;
        ListState<Long> listState;
        /**
         * 将本地变量拷贝到状态 持久化
         * TODO 一个线程触发一次 只有在首次运行时触发
         * @param context 上下文
         * @throws Exception e
         */
        public void snapshotState(FunctionSnapshotContext context) throws Exception
        {
            System.out.println("snapshotState被调用");
            //TODO 清空算子状态
            listState.clear();
            //TODO 放入算子状态
            listState.add(conunt);
        }

        /**
         * 初始本地变量 在down重启后
         * @param context 上下文
         * @throws Exception e
         */
        public void initializeState(FunctionInitializationContext context) throws Exception
        {
            System.out.println("initializeState被调用");
            //TODO 使用上下文获取算子状态
            //TODO 由于我们情况了又放入 所以每个线程算子状态列表始终只有一个值
            //TODO 将状态值取出后累加

            //TODO 判断是否恢复成功 true false 直意
            if (context.isRestored())
            {
                Iterable<Long> le = context
                    .getOperatorStateStore()
                    .getListState(new ListStateDescriptor<Long>("LE", Types.LONG))
                    .get();
                le.forEach(f -> conunt += f);
            }
        }
    }
}
