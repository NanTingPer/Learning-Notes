import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class Test2
{
    public static void main(String[] args)
    {
        //配置文件
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "bigdata1:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "Proeter");
        KafkaProducer<String,String> kfpPack = new KafkaProducer<>(config);
        for(int i = 0;i< 100;i++){
            kfpPack.send(new ProducerRecord<String, String>("Demo","a",Integer.toString(i)),new Callback()
            {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e)
                {
                    if(e == null){
                        System.out.println(recordMetadata.topic() + " " + recordMetadata.partition());
                    }
                }
            });
        }
        kfpPack.close();
    }
}
