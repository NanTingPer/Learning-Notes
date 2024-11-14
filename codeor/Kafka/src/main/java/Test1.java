import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class Test1
{
    public static void main(String[] args)
    {
        //创建Kafka的配置文件
        Properties prodCofig = new Properties();
        prodCofig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.45.13:9092");
        //键值序列化
        prodCofig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        prodCofig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        KafkaProducer<String,String> Proud =  new KafkaProducer<>(prodCofig);
        for(int i = 0 ;i < 10 ;i++)
        {
            Proud.send(new ProducerRecord<>("Demo",Integer.toString(i)), new Callback()
            {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e)
                {
                    if(e == null){
                        System.out.println(recordMetadata.topic());
                    }
                }
            });
        }
        Proud.close();
    }
}
