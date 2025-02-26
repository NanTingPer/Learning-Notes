package WordCountStream;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class WordCount
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> socketData = see.socketTextStream("127.0.0.1", 7777);
        socketData.flatMap((String s, Collector<Tuple2<String, Integer>> collector) -> {
            String[] s1 = s.split(" ");
            for (String string : s1)
            {
                collector.collect(new Tuple2<String,Integer>(string,1));
            }
            // 在这里写入转换逻辑
        })//泛型擦除 自己指定类型
                .returns(Types.TUPLE(Types.STRING,Types.INT))
                .keyBy(v -> {return  v.f0;})
                .sum(1)
                .print();
        see.execute();
    }
}
