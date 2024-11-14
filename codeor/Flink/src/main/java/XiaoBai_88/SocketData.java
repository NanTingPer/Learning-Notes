package XiaoBai_88;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class SocketData
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<String> stringDataStreamSource = Env.socketTextStream("192.168.45.13", 25001);
        stringDataStreamSource.print();

        Env.execute();
    }
}
