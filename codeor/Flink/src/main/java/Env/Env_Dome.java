package Env;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class Env_Dome
{
    public static void main(String[] args) throws Exception
    {
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //配置文件
        Configuration conf = new Configuration();
        //Flink封装了一个有关配置的类
        //          RestOptions
        conf.set(RestOptions.BIND_PORT,"8081");
        //使用配置文件获取运行环境 未配置的配置项 按照默认值
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment(conf);

        //流处理
        Env.setRuntimeMode(RuntimeExecutionMode.STREAMING);


        Env.executeAsync();
        Env.execute();

    }
}
