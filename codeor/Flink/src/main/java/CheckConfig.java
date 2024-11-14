import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.time.Duration;

public class CheckConfig
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        System.setProperty("HADOOP_USER_NAME","root");

        //TODO 默认barrier对齐 第一个参数是设置多久一次ms , 第二个参数是模式 精准一次
        Env.enableCheckpointing(5000, CheckpointingMode.EXACTLY_ONCE);

        //TODO 获取配置文件
        CheckpointConfig config = Env.getCheckpointConfig();

        //TODO 设置保存路径
        config.setCheckpointStorage("hdfs://192.168.45.13:8020/chk");

        //TODO checkpoint的超时时间 默认10分组
        config.setCheckpointTimeout(60000);

        //TODO 同时运行中的checkpoint的最大数
        config.setMaxConcurrentCheckpoints(2);

        //TODO 最小等待间隔 上一轮checkpoint结束到下一轮checkpoint开始 之间的间隔 设置>0并发就会变成1
        config.setMinPauseBetweenCheckpoints(1000);

        //TODO 取消作业时 checkpoint的数据是否保留在外部系统  => 在取消时删除
        //TODO 程序突然挂掉 无法删除
        config.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.DELETE_ON_CANCELLATION);

        //TODO 运行checkpoint最大失败次数 默认0,如果失败job（作业）就挂掉
        config.setTolerableCheckpointFailureNumber(10);

        //TODO 开启非对其检查点 必须精准一次才能
        //TODO setMaxConcurrentCheckpoints必须为1
        config.enableUnalignedCheckpoints();

        //TODO 如果大于0 ，非对齐检查点启用时候，先使用对齐检查点
        //TODO 如果对齐检查点超时 超过设定时间 切换为非对齐 Flink 16后
        config.setAlignedCheckpointTimeout(Duration.ofSeconds(1));

        Env.execute();
    }
}
