package State_;

import WaterSensors.WaterSensor;
import functions.WaterSensorMapFunction;
import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.BroadcastConnectedStream;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;

public class BroadState
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        SingleOutputStreamOperator<WaterSensor> Data = Env
            .socketTextStream("192.168.45.13", 7777)
            .map(new WaterSensorMapFunction());

        //TODO 定义一个流 用来充当广播流
        DataStreamSource<String> Brod = Env.socketTextStream("192.168.45.13", 8888);
        //TODO 创建广播状态
        MapStateDescriptor<String, Integer> MapStateStream = new MapStateDescriptor<>("Broadcast", Types.STRING, Types.INT);
        //TODO 将流加入广播状态
        BroadcastStream<String> broadcast = Brod.broadcast(MapStateStream);
        //TODO 将需要共享广播状态的流与广播流进行合并
        BroadcastConnectedStream<WaterSensor, String> BrodAllStream = Data.connect(broadcast);

        //TODO 第一个泛型 第一条流的类型
        //TODO 第二个泛型 第二条流的类型
        //TODO 第三个泛型 输出的类型
        BrodAllStream.process(new BroadcastProcessFunction<WaterSensor, String, String>() {

            @Override
            public void processElement(WaterSensor value, BroadcastProcessFunction<WaterSensor, String, String>.ReadOnlyContext ctx, Collector<String> out) throws Exception
            {
                //TODO 取得广播流数据
                Integer MAX = ctx
                    .getBroadcastState(MapStateStream)
                    .get("runErr");
                //TODO 判断广播流数据是否为空
                int IsExNULL = (MAX == null ? 0 : MAX);
                if(value.getVc() > IsExNULL) out.collect(value + "\t" + "水位过高 报警");
            }

            /**
             *广播流的数据处理
             * @param value 传入的值
             * @param ctx 上下文 可以用来更新广播状态
             * @param out 采集器
             * @throws Exception e
             */
            @Override
            public void processBroadcastElement(String value, BroadcastProcessFunction<WaterSensor, String, String>.Context ctx, Collector<String> out) throws Exception
            {
                //TODO 得到流
                BroadcastState<String, Integer> ErrStream = ctx.getBroadcastState(MapStateStream);
                //TODO 覆写数据
                ErrStream.put("runErr",Integer.valueOf(value));
            }
        }).print();

        Env.execute();
    }
}
