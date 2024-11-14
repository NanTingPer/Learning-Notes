import org.apache.kafka.clients.KafkaClient;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class Conset
{
    public static void main(String[] args)
    {
        Properties props = new Properties();
        //连接集群
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.45.10:9092");
        //指定序列化类型
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //创建Kafka生产者对象
        KafkaProducer<String, String> Kafkapri = new KafkaProducer<>(props);

        //发送数据
        for (int i = 0; i < 10; i++){
            System.out.println("第" + i + "次");
            Kafkapri.send(new ProducerRecord<>("dome","eeee"));
        }
        Kafkapri.close();
    }
}
