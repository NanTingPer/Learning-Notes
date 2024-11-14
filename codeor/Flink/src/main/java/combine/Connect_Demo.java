package combine;

import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;

public class Connect_Demo
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        SingleOutputStreamOperator<Integer> DataS1 =
            Env.socketTextStream("192.168.45.13", 7777)
                .map(f -> {
                    Integer b = 0;
                    try{
                    b = Integer.valueOf(f);}
                    catch (Exception e)
                    {
                        b = 0;
                    }
                    return b;
                });
        DataStreamSource<String> DataS2 = Env.socketTextStream("192.168.45.13", 8888);

        ConnectedStreams<Integer, String> DataCom = DataS1.connect(DataS2);

        SingleOutputStreamOperator<String> connMap = DataCom.map(new CoMapFunction<Integer, String, String>()
        {
            @Override
            public String map1(Integer integer) throws Exception
            {
                return "来源于数字流:" + integer;
            }

            @Override
            public String map2(String s) throws Exception
            {
                return "来源于字符流:" + s;
            }
        });

        connMap.print();

        Env.execute();

    }
}
