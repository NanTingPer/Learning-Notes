package functions;

import WaterSensors.WaterSensor;
import org.apache.flink.api.common.functions.MapFunction;

public class MapFun implements MapFunction<WaterSensor,String>
{

    @Override
    public String map(WaterSensor waterSensor) throws Exception
    {
        return waterSensor.getId();
    }
}
