package OutStream;

import WaterSensors.WaterSensor;
import functions.WaterSensorMapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SideOutputDataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

public class ProcessOutStream
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<String> DataStream = Env.socketTextStream("192.168.45.13", 7777);

        //将输入的数据转换为WaterSensor
        SingleOutputStreamOperator<WaterSensor> map = DataStream.map(new WaterSensorMapFunction());

        //创建两个支流
        //第一个参数是支流名称
        //第二个参数是该流内的数据类型
        OutputTag<WaterSensor> s1 = new OutputTag<>("s1", Types.POJO(WaterSensor.class));
        OutputTag<WaterSensor> s2 = new OutputTag<>("s2", Types.POJO(WaterSensor.class));

        //分流
        SingleOutputStreamOperator<WaterSensor> Value = map.process(new ProcessFunction<WaterSensor, WaterSensor>()
        {

            //第一个参数是传入的数据
            //第二个参数是上下文
            //第三个参数是给下游发送消息的(主干道)
            @Override
            public void processElement(WaterSensor waterSensor, ProcessFunction<WaterSensor, WaterSensor>.Context context, Collector<WaterSensor> collector) throws Exception
            {
                //如果id是s1那么走支流s1
                if ("s1".equals(waterSensor.getId()))
                {
                    context.output(s1, waterSensor);
                }
                //如果id是s2那么走支流s2
                else if ("s2".equals(waterSensor.getId()))
                {
                    context.output(s2, waterSensor);
                }
                //其他走主干
                else
                {
                    collector.collect(waterSensor);
                }
            }
        });

        //输出主干道内容
        Value.print("主干道\t");
        //输出支流
        SideOutputDataStream<WaterSensor> s1Value = Value.getSideOutput(s1);
        s1Value.print("支s1\t");
        SideOutputDataStream<WaterSensor> s2Value = Value.getSideOutput(s2);
        s2Value.print("支s2\t");

        Env.execute();

    }
}
