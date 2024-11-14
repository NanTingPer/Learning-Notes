import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.*;


public class 消费者
{
    public static void main(String[] args)
    {
        Map<String,Object> config = new HashMap<String,Object>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        //设置消费者组
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "group1");

        KafkaConsumer<String,String> consumer = new KafkaConsumer<>(config);
        //指定要消费的主题
        consumer.subscribe(Arrays.asList("topic1"));
        //自定义数据偏移量
        //用来决定退出循环
        boolean flag = true;
        while (flag)
        {
            //拉取
            consumer.poll(Duration.ofMillis(100));
            //获取当前消费者已分配的分区列表
            Set<TopicPartition> 分区表 = consumer.assignment();
            //不为空 并且 包含元素 isEmpty用于判断是否包含元素，不包含返回true 所以需要取反
            if(分区表 != null && !分区表.isEmpty())
            {
                for (TopicPartition topicPartition : 分区表)
                {
                    //获取分区所属主题名称
                    if(topicPartition.topic().equals("topic1"))
                    {
                        //将消费者的数据偏移量设置为指定值
                        consumer.seek(topicPartition,1);
                        flag = false;
                    }
                }
            }
        }
        //100毫秒
        while(true)
        {
            ConsumerRecords<String, String> poll = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : poll){
                System.out.println(record.value());
            }
        }
    }
}
