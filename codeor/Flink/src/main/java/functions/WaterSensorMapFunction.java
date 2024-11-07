package functions;

import WaterSensors.WaterSensor;
import org.apache.flink.api.common.functions.MapFunction;

public class WaterSensorMapFunction implements MapFunction<String, WaterSensor>
{
    @Override
    public WaterSensor map(String s) throws Exception
    {
        String[] split = s.split(",");
        String id;
        Long ts;
        int vc;
        id = split[0];
        try{
            ts = Long.valueOf(split[1]);
        }catch (Exception e){ ts = 0L;}

        try{
            vc = Integer.valueOf(split[2]);
        }catch (Exception e){ vc = 0;}
        return new WaterSensor(id,ts,vc);
    }
}
