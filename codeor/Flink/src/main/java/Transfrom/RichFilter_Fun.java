package Transfrom;

import WaterSensors.WaterSensor;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class RichFilter_Fun
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        Env.setParallelism(2);

        DataStreamSource<WaterSensor> Data = Env.fromElements(
            new WaterSensor("sd1", 2L, 3),
            new WaterSensor("sd1", 343L, 3),
            new WaterSensor("sd2", 2L, 3),
            new WaterSensor("sd3", 2L, 3)
        );

        Data.filter(new RichFilterFunction<WaterSensor>() {
            @Override
            public boolean filter(WaterSensor waterSensor) throws Exception
            {
                return true;
            }

            @Override
            public void open(Configuration parameters) throws Exception
            {
                super.open(parameters);
                System.out.println(getRuntimeContext().getTaskNameWithSubtasks() + "调用了open");
            }

            @Override
            public void close() throws Exception
            {
                super.close();
                System.out.println(getRuntimeContext().getTaskNameWithSubtasks() + "调用了close");
            }
        }).print();

        Env.execute();
    }
}
