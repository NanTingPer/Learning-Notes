package _001;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class WordCount
{
    public static void main(String[] args) throws Exception
    {
        //创建运行环境
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();

        //使用文本文件形成DataStream 数据流
        DataStreamSource<String> WordFile = see.readTextFile("C:\\LiMGren\\codeor\\Flink\\src\\main\\java\\_001\\word.txt");

        //调用flatMap进行数据类型转换 转换为(string,1)
        SingleOutputStreamOperator<Tuple2<String, Integer>> wordSplit = WordFile.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>()
        {

            @Override
            public void flatMap(String s, Collector<Tuple2<String, Integer>> collector) throws Exception
            {
                //使用空格分组
                String[] Word = s.split(" ");
                for (String string : Word)
                {
                    //调用采集器发送数据 最后会被变量收到
                    collector.collect(new Tuple2<String, Integer>(string, 1));
                }
            }
        });

        //按照Key进行分组
        KeyedStream<Tuple2<String, Integer>, String> All = wordSplit.keyBy(new KeySelector<Tuple2<String, Integer>, String>()
        {
            @Override
            public String getKey(Tuple2<String, Integer> stringIntegerTuple2) throws Exception
            {
                return stringIntegerTuple2.f0;
            }
        });

        SingleOutputStreamOperator<Tuple2<String, Integer>> AllOut = All.sum(1);
        AllOut.print();

        //行动算子
        see.execute();

    }
}
