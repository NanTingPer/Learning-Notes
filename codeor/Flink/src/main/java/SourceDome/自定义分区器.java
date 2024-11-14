package SourceDome;

import org.apache.flink.api.common.functions.Partitioner;

public class 自定义分区器 implements Partitioner
{
    @Override
    public int partition(Object o, int i)
    {
        if(o.hashCode() % i <= 0) return 0;
        if(o.hashCode() % i > i) return i;
        return o.hashCode() % i - 1;
    }
}
