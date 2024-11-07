package aggreagte;

import WaterSensors.WaterSensor;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class KeyByFun
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();
//        Env.setParallelism(2);//设置并行度
        DataStreamSource<WaterSensor> Data = Env.fromElements(
            new WaterSensor("sd1", 2L, 3),
            new WaterSensor("sd1", 3L, 7),
            new WaterSensor("sd2", 2L, 8),
            new WaterSensor("sd2", 6L, 5),
            new WaterSensor("sd3", 20L, 14),
            new WaterSensor("sd3", 18L, 5)
        );
        //按照 id 分组
        //KeySelector<输入的类型,分组字段的类型>
        Data.keyBy(new KeySelector<WaterSensor, String>() {
            @Override
            public String getKey(WaterSensor waterSensor) throws Exception
            {
                return waterSensor.getId();
            }
        }).maxBy("vc").print();
        Env.execute();
    }
}
