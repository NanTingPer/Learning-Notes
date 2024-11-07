package SourceDome;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Arrays;

public class CollectionDome
{
    public static void main(String[] args) throws Exception
    {
        //环境
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        //获取
        DataStreamSource<Integer> ArraysSource = Env.fromCollection(Arrays.asList(1, 1, 2, 34, 5, 64, 6));

        //打印
        ArraysSource.print();

        Env.execute();
    }
}
