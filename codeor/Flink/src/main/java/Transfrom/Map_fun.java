package Transfrom;

import WaterSensors.WaterSensor;
import functions.MapFun;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class Map_fun
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<WaterSensor> Data = Env.fromElements(
            new WaterSensor("sd1", 2L, 3),
            new WaterSensor("sd2", 2L, 3),
            new WaterSensor("sd3", 2L, 3)
        );

        Data.map(new MapFun()).print();

        Env.execute();
    }
}
