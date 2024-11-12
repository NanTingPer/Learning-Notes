package FlinkSQL;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class WordCount
{
    public static void main(String[] args) throws Exception
    {
        //TODO 1.创建FlinkEnv
        StreamExecutionEnvironment flinkEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        //TODO 2.使用FlinkEnv创建TableEnv
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(flinkEnv);


        //TODO 3.创建一个socket流
        DataStreamSource<String> data = flinkEnv.socketTextStream("192.168.45.13", 7777);

        //TODO 4.将socket流数据转换为2元组
        DataStream<Tuple2<String, Integer>> map = data.map(f ->
        {
            String[] split = f.split(",");
            return new Tuple2<>(split[0], Integer.valueOf(split[1]));
            //TODO 注意泛型擦除
        }).returns(Types.TUPLE(Types.STRING,Types.INT));
        //TODO 创建视图 指定列名
        tableEnv.createTemporaryView("mapTable",map,org.apache.flink.table.api.Expressions.$("Word"),org.apache.flink.table.api.Expressions.$("num"));


        //TODO 调用TableEnv的sql查询
        //TODO 分组求和
        Table table = tableEnv.sqlQuery("select Word,sum(num) from mapTable group by Word");

        //TODO 将表转流打印
        tableEnv.toChangelogStream(table).print();

        flinkEnv.execute();

    }
}
