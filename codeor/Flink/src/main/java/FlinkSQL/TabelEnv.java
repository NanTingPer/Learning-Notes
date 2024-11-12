package FlinkSQL;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class TabelEnv
{
    public static void main(String[] args) throws Exception
    {
        //TODO 1.创建FlinkEnv
        StreamExecutionEnvironment flinkEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        //TODO 2.使用FlinkEnv创建TableEnv
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(flinkEnv);

        //TODO 3.创建一个socket流
        DataStreamSource<String> data = flinkEnv.socketTextStream("192.168.45.13", 7777);

        //TODO 4.将socket流转换为表视图
        //TODO 第一个参数是视图名，第二个是流
        tableEnv.createTemporaryView("socket",data);

        //TODO 调用TableEnv的sql查询
        Table table = tableEnv.sqlQuery("select * from socket");

        //TODO 将表转流打印
        tableEnv.toChangelogStream(table).print();

        table.execute().print();

//        flinkEnv.execute();

    }
}
