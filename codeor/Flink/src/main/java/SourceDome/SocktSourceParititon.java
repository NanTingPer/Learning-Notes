package SourceDome;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class SocktSourceParititon
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        Env.setParallelism(5);

        DataStreamSource<String> Data = Env.socketTextStream("192.168.45.13", 7777);

        Data.partitionCustom(
            new 自定义分区器(), k -> k
        ).print();

        Env.execute();
    }
}
