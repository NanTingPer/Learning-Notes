import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

public class Proeter implements Partitioner
{
    @Override
    public int partition(String s, Object o, byte[] bytes, Object o1, byte[] bytes1, Cluster cluster)
    {
        int num = 0;
        if(o1.toString().equals("2")){
            num = 1;
        }else{
            num = 0;
        }
        return num;
    }

    @Override
    public void close()
    {

    }

    @Override
    public void configure(Map<String, ?> map)
    {

    }
}
