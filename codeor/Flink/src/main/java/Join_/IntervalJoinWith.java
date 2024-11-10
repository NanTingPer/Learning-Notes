package Join_;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

public class IntervalJoinWith
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();
        Env.setParallelism(1);

        SingleOutputStreamOperator<Tuple2<String, Integer>> tuple2 = Env
            .socketTextStream("192.168.45.13",7777)
            .map(f->{String[] e = f.split(",");return new Tuple2<String,Integer>(e[0],Integer.valueOf(e[1]));})
            .returns(Types.TUPLE(Types.STRING,Types.INT))
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
            .socketTextStream("192.168.45.13",8888)
            .map(f -> {String[] s =  f.split(",");return new Tuple3<String,Integer,Integer>(s[0],Integer.valueOf(s[1]),Integer.valueOf(s[2])); })
            .returns(Types.TUPLE(Types.STRING,Types.INT,Types.INT))
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

        //创建两条侧输出流
        OutputTag<Tuple2<String,Integer>> tag1 = new OutputTag<>("tag1", Types.TUPLE(Types.STRING,Types.INT));
        OutputTag<Tuple3<String,Integer,Integer>> tag2 = new OutputTag<>("tag2", Types.TUPLE(Types.STRING,Types.INT,Types.INT));


        // TODO 必须先KeyBy才能使用intervalJoin
        KeyedStream<Tuple3<String, Integer, Integer>, String> tp3key = tuple3.keyBy(f -> f.f0);
        KeyedStream<Tuple2<String, Integer>, String> tp2key = tuple2.keyBy(f -> f.f0);

        // TODO 使用intervalJoin
        SingleOutputStreamOperator<String> run = tp2key.intervalJoin(tp3key)
               // TODO 使用between设置时间偏移量 以数据自身的事件时间为基准
               // TODO 匹配对方在设定时间内的数据
               .between(Time.seconds(-3), Time.seconds(3))
               //左数据
               .sideOutputLeftLateData(tag1)
               //右数据
               .sideOutputRightLateData(tag2)
               // TODO 匹配后的数据
               .process(new ProcessJoinFunction<Tuple2<String, Integer>, Tuple3<String, Integer, Integer>, String>()
               {
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
                       collector.collect(stringIntegerTuple2 + "\t\t\t" + stringIntegerIntegerTuple3);
                   }
               });

        run.print("主流");
        run.getSideOutput(tag1).printToErr("tup2迟到");
        run.getSideOutput(tag2).printToErr("tup3迟到");


        Env.execute();
    }
}
