package functions;

import org.apache.flink.api.common.eventtime.Watermark;
import org.apache.flink.api.common.eventtime.WatermarkGenerator;
import org.apache.flink.api.common.eventtime.WatermarkOutput;

public class MyWatermarkStrategy<T> implements WatermarkGenerator<T>
{
    /**
     *timeInter = 乱序等待时间
     *new Time = 新的时间
     */
    private long timeInter;
    private long newTime;

    //设置空参构造，不传值就 给个3秒的延迟
    public MyWatermarkStrategy(){
        this.timeInter = 3000L;
        this.newTime = Long.MIN_VALUE + 3001L;
    }

    public MyWatermarkStrategy(long timeInter) {
        this.timeInter = timeInter;
        this.newTime = Long.MIN_VALUE + timeInter + 1;
    }

    /**
     * 每次有数据来就执行一次
     * @param t e
     * @param l 提取到的数据的事件时间
     * @param watermarkOutput e
     */
    @Override
    public void onEvent(T t, long l, WatermarkOutput watermarkOutput)
    {
        //获取最大的水位线
        this.newTime = Math.max(l,newTime);
    }

    /**
     * 用来将数据输出 即判断是否到达水位线
     * @param watermarkOutput e
     */
    @Override
    public void onPeriodicEmit(WatermarkOutput watermarkOutput)
    {
        watermarkOutput.emitWatermark(new Watermark(this.newTime - this.timeInter - 1));
    }
}
