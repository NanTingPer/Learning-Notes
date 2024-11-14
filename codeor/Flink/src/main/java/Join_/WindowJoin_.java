package Join_;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkGenerator;
import org.apache.flink.api.common.eventtime.WatermarkGeneratorSupplier;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.CoGroupedStreams;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.Window;

public class WindowJoin_
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();
        Env.setParallelism(1);

        SingleOutputStreamOperator<Tuple2<String, Integer>> tuple2 = Env
            .fromElements(
                new Tuple2<>("a", 1),
                new Tuple2<>("a", 2),
                new Tuple2<>("c", 10),
                new Tuple2<>("b", 10),
                new Tuple2<>("c", 115)
            )
            .assignTimestampsAndWatermarks(WatermarkStrategy
                .<Tuple2<String, Integer>>forMonotonousTimestamps()
                .withTimestampAssigner(new SerializableTimestampAssigner<Tuple2<String, Integer>>()
                {
                    @Override
                    public long extractTimestamp(Tuple2<String, Integer> stringIntegerTuple2, long l)
                    {
                        return stringIntegerTuple2.f1 * 1000L;
                    }
                }));


        //Tuple3数据源
        SingleOutputStreamOperator<Tuple3<String, Integer, Integer>> tuple3 = Env
            .fromElements(
                new Tuple3<>("a", 1, 2),
                new Tuple3<>("b", 5, 2),
                new Tuple3<>("a", 6, 2),
                new Tuple3<>("c", 7, 2),
                new Tuple3<>("a", 10, 2),
                new Tuple3<>("a", 15, 2),
                new Tuple3<>("a", 23, 2)
            )
            //TODO 设置水位线逻辑
            .assignTimestampsAndWatermarks(WatermarkStrategy
                //TODO 升序水位线
                .<Tuple3<String, Integer, Integer>>forMonotonousTimestamps()
                //TODO 水位线设置水位线字段
                .withTimestampAssigner(new SerializableTimestampAssigner<Tuple3<String, Integer, Integer>>()
                {
                    @Override
                    public long extractTimestamp(Tuple3<String, Integer, Integer> stringIntegerIntegerTuple3, long l)
                    {
                        return stringIntegerIntegerTuple3.f1 * 1000L;
                    }
                }));

        tuple2
            // TODO 要join的另一条流
            .join(tuple3)
            //TODO join左边的键
            .where(k -> k.f0)
            //TODO join右边的键
            .equalTo(k -> k.f0)
            //TODO 开窗 JOIN只会在该窗口内匹配 非窗口的数据不匹配
            .window(TumblingEventTimeWindows.of(Time.seconds(10)))
            //TODO 进JOIN
            .apply(new JoinFunction<Tuple2<String, Integer>, Tuple3<String, Integer, Integer>, String>()
            {
                // TODO 数据匹配后的join逻辑

                /**
                 * 关联上的数据
                 * @param stringIntegerTuple2 数据1
                 * @param stringIntegerIntegerTuple3 数据2
                 * @return 自定义返回
                 * @throws Exception e
                 */
                @Override
                public String join(Tuple2<String, Integer> stringIntegerTuple2, Tuple3<String, Integer, Integer> stringIntegerIntegerTuple3) throws Exception
                {
                    return stringIntegerTuple2 + "\t \t \t" + stringIntegerIntegerTuple3;
                }
            }).print();

        Env.execute();
    }
}
