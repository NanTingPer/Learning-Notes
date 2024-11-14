package admin;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.internals.Topic;

import java.util.*;

public class KafkaTopic
{
    public static void main(String[] args) throws Exception
    {
        //创建配置文件 键值对
        Map<String, Object> config = new HashMap<>();

//          topic名，键，值
//        ProducerRecord<String,String> PR = new ProducerRecord<>()
        //添加链接配置
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.45.13:9092");
        //创建管理员用户
        Admin admin = Admin.create(config);
        //创建新的主题
        String TopicName = "DemoTopic1";
        int Partitions = 1;
        short Replicas = 1;
        NewTopic newTopic = new NewTopic(TopicName, Partitions, Replicas);
        //创建主题 topics
        CreateTopicsResult topics = admin.createTopics(Arrays.asList(newTopic));
        //关闭链接
        admin.close();
    }
}
