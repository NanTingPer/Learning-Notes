package combine;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class Union_Demo
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<Integer> Data1 = Env.fromElements(1, 2, 3);
        DataStreamSource<Integer> Data2 = Env.fromElements(11, 22, 33);
        DataStreamSource<String> Data3 = Env.fromElements("1", "2", "3");

        DataStream<Integer> union1 = Data1.union(Data2).union(Data3.map(f -> Integer.valueOf(f)));
        DataStream<Integer> union2 = Data1.union(Data2, Data3.map(f -> Integer.valueOf(f)));

        union2.print();

        Env.execute();

    }
}
