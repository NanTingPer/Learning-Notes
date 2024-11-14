package OutTrans;

import WaterSensors.WaterSensor;
import functions.WaterSensorMapFunction;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.jdbc.JdbcStatementBuilder;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OutJDBC
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

        Env.enableCheckpointing(500, CheckpointingMode.EXACTLY_ONCE);

        //Socket数据源
        SingleOutputStreamOperator<WaterSensor> socketSource = Env.socketTextStream("192.168.45.13", 7777)
                                                         .map(new WaterSensorMapFunction());

        SinkFunction<WaterSensor> jdbc = JdbcSink.sink("insert into ws values(?,?,?)", new JdbcStatementBuilder<WaterSensor>()
        {
            /**
             *
             * @param preparedStatement 占位符填充
             * @param waterSensor   传入的数据
             * @throws SQLException >
             */
            @Override
            public void accept(PreparedStatement preparedStatement, WaterSensor waterSensor) throws SQLException
            {
                //填充上面的? 索引从1开始
                preparedStatement.setString(1, waterSensor.getId());
                preparedStatement.setLong(2, waterSensor.getTs());
                preparedStatement.setInt(3, waterSensor.getVc());
            }
            //创建JDBC配置文件
        }, new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
            .withPassword("123456")
            .withUrl("jdbc:mysql://192.168.45.13:3306/test?useSSL=false")
            .withUsername("root")
            //超时时间
            .withConnectionCheckTimeoutSeconds(60)
            .build());

        //将数据写入Sink
        socketSource.addSink(jdbc);

        Env.execute();
    }
}
