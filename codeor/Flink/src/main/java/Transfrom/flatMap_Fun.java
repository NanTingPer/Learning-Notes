package Transfrom;

import WaterSensors.WaterSensor;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class flatMap_Fun
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<WaterSensor> Data = Env.fromElements(
            new WaterSensor("sd1", 2L, 3),
            new WaterSensor("sd1", 3L, 7),
            new WaterSensor("sd2", 2L, 8),
            new WaterSensor("sd2", 6L, 5),
            new WaterSensor("sd3", 8L, 14),
            new WaterSensor("sd3", 9L, 5)
        );
        //如果是sd1输出ts
        //如果是sd2输出ts和vc

        Data.flatMap(new FlatMapFunction<WaterSensor, String>() {
            @Override
            public void flatMap(WaterSensor waterSensor, Collector<String> collector) throws Exception
            {
                if("sd1".equals(waterSensor.getId())){
                    collector.collect(waterSensor.getId() + "\tts: " +  waterSensor.ts);
                }else if ("sd2".equals(waterSensor.getId())) {
                    collector.collect(waterSensor.getId() + "\tts: " +  waterSensor.ts);
                    collector.collect(waterSensor.getId() + "\tvc: " +  waterSensor.vc);
                }
            }
        }).print();

        Env.execute();
    }
}
