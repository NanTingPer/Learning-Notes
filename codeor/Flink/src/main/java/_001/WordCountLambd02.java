package _001;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class WordCountLambd02
{
    public static void main(String[] args) throws Exception
    {
        //获取运行环境
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();

        //创建Socket流
        DataStreamSource<String> socketStream = see.socketTextStream("192.168.45.13", 7777);

//        socketStream.flatMap((String str, DataStream.Collector<Tuple2<String,Integer>> a) ->{
//
//        })
        //进行类型转换
        socketStream.flatMap((String str,Collector<Tuple2<String,Integer>> col) ->{
            String[] split = str.split(" ");
            for (String s : split)
            {
                //发送给下游
                col.collect(new Tuple2<String,Integer>(s,1));
            }
            //解决类型擦除
        }).returns(Types.TUPLE(Types.STRING,Types.INT))
            .keyBy(kv->kv.f0)
            .sum(1)
        .print();

        see.execute();
    }
}
