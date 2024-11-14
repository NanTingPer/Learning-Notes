package Join_;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkGenerator;
import org.apache.flink.api.common.eventtime.WatermarkGeneratorSupplier;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.CoGroupedStreams;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.Window;
import org.apache.flink.util.Collector;

public class IntervalJoin
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

        // TODO 必须先KeyBy才能使用intervalJoin
        KeyedStream<Tuple3<String, Integer, Integer>, String> tp3key = tuple3.keyBy(f -> f.f0);
        KeyedStream<Tuple2<String, Integer>, String> tp2key = tuple2.keyBy(f -> f.f0);

        // TODO 使用intervalJoin
        tp2key.intervalJoin(tp3key)
                    // TODO 使用between设置时间偏移量 以数据自身的事件时间为基准
                    // TODO 匹配对方在设定时间内的数据
                  .between(Time.seconds(-3),Time.seconds(3))
                    // TODO 匹配后的数据
                  .process(new ProcessJoinFunction<Tuple2<String, Integer>, Tuple3<String, Integer, Integer>, String>() {
                      /**
                       * 匹配上后执行
                       * @param stringIntegerTuple2 左流数据
                       * @param stringIntegerIntegerTuple3 右流数据
                       * @param context 上下文
                       * @param collector coll
                       * @throws Exception e
                       */
                      @Override
                      public void processElement(Tuple2<String, Integer> stringIntegerTuple2, Tuple3<String, Integer, Integer> stringIntegerIntegerTuple3, ProcessJoinFunction<Tuple2<String, Integer>, Tuple3<String, Integer, Integer>, String>.Context context, Collector<String> collector) throws Exception
                      {
                          collector.collect(stringIntegerTuple2 + "\t\t\t" + stringIntegerIntegerTuple3 );
                      }
                  }).print();


        Env.execute();
    }
}
