import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

public class Value_Interceptor implements ProducerInterceptor<String, String>
{

    @Override
    //拦截
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> pr)
    {
        return new ProducerRecord<>(pr.topic(), pr.key(), pr.value() + "awf");
    }

    @Override
    //消息发送完后服务器响应 调用
    public void onAcknowledgement(RecordMetadata recordMetadata, Exception e)
    {

    }

    @Override
    //关闭后
    public void close()
    {

    }

    @Override
    //配置文件处理
    public void configure(Map<String, ?> map)
    {

    }
}