import org.apache.flink.streaming.api.scala.DataStream;
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment;

public class LiunxTest {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<String> Data = env.socketTextStream("192.168.45.13", 7777, '\n',0);
        Data.print();

        env.execute();
    }
}
