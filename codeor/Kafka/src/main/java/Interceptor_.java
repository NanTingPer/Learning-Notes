import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.Map;

public class Interceptor_
{
    public static void main(String[] args)
    {
        Map<String,Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.45.13:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //指定拦截器
        config.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG,Value_Interceptor.class.getName());

        KafkaProducer<String,String> kafka = new KafkaProducer<>(config);
        ProducerRecord<String,String> prss = new ProducerRecord<String,String>("Demo","verl");

        kafka.send(prss, new Callback()
        {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e)
            {
                System.out.println(recordMetadata.topic());
            }
        });

        kafka.close();
    }
}
